/*
File : UGate.pde
Author : Will Hoover
Version : v1.0, 10-11-2011

Local Devices:
- Arduino Uno
- XL-MaxSonar EZ3 MB1230 (~25ft Range)
- XBee Adapter with XBee Pro 60mW Chip Antenna (~1 Mile Range)
- Serial CCD Electronic Brick Camera (Seed Studio)
- TSOP382 38 kHz IR Reciever (Newark)
- Ultra-Bright IR LED (Newark)
- Ultra-Bright Tri-Color RGB 5mm LED (EBay or RadioShack)
- HD44760 based LCD 20x4 chars (optional)

Remote Devices:
- XBee Adaptor/USB to TTL UART 6PIN CP2102 with XBee Pro 60mW Chip Antenna (~1 Mile Range to PC Interface)
- GE 24944 Universal Remote (or any universal remote control)

Description:
Sonar, IR, and microwave are constantly checking the corresponding distance/speed thresholds. When surpassed it will initiate an
on-board camera to take a picture and start streaming the JPEG compressed data over XBee 
until the EOF (0xFF, 0xD9).
*/
#include <Servo.h>
#include <XBee.h>
#include "Ugate.h"
//======= Arduino Main =======
void setup() {
  // Indicator RGB LED (LOW is on)
  LEDWRITE(HIGH, HIGH, HIGH);
  pinMode(LED_RED_PIN, OUTPUT);
  pinMode(LED_BLUE_PIN, OUTPUT);
  pinMode(LED_GREEN_PIN, OUTPUT);
  // Camera:
  Serial1.begin(115200);
  // XBee: unsuccessful xbee attempts are automatically retried at host computer
  // tested at 100% success rate for baud rate 9600 (avg VGA 20 sec)
  // tested at ~100% success rate for baud rate 19200 (avg VGA 10 sec/avg QVGA 3 sec)
  // tested at ~80% success rate for baud rate 38400 (avg VGA 7 sec/avg QVGA <1 sec)
  xbee.begin(19200);
  Serial.begin(19200);
  //xbee.setSerial(Serial);
  // Infrared (for Universal Remote):
  pinMode(SONAR_TX_PIN, OUTPUT);
  irSetup();
  mwSetup();
}
void loop() {
  execBufferCmds();
  xbeeRead();
  sonarRead(true);
  //irRead(true);
  //mwReadFreq(true);
  LEDOFF();
  SERVODETACH();
}
// Removes/Returns the first element in the buffer and presses the specified element to the end of the buffer 
int pressBuffer(int buffer[], int numElem, int* press) {
  int pop = *(buffer);
  int sz = sizeof(*buffer);
  memmove(buffer, buffer + 1, (numElem - 1) * sz);
  memcpy(buffer + (numElem - 1), press, sz);
  return pop;
}
int irTestValue;
int irTripCmd = 255;
// Executes the buffered commands in the order they are recieved
void execBufferCmds() {
  LEDOFF();
  int cmd;
  int numElem = NUMOFELEM(cmdBuffer);
  for (short i=0; i<numElem; i++) {
    cmd = pressBuffer((int*) &cmdBuffer, numElem, (int*) &cmdNA);
    if (cmd > 0 && cmd != cmdNA) {
      LEDWRITE(HIGH, HIGH, LOW);
      if (SERVOISCMD(cmd)) {
        // move the camera, sonar/IR armature, or microwave (which one depends on servoSelect)
        SERVOMOVE(cmd);
      } else if (cmd == CMD_SESSION_RESET) {
        // reset session
        irKeys[0] = irKeys[1] = irKeys[2] = cmdNA;
      } else if (cmd == CMD_TAKE_SEND_PIC) {
        // take/send picture in the predefined resolution and sends the data over xbee (along with sensor readings)
        camTakeSendPic(cmd);
      } else if (cmd == CMD_TOGGLE_ALARMS) {
        // TODO : turn alarms on/off
      } else if (cmd == CMD_TOOGLE_SERVO) {
        // toggle between camera and sonar/IR armature servos
        SERVOTOGGLE();
      } else if (cmd == CMD_TOOGLE_GATE) {
        // TODO :  open/close gate (if applicable)
      } else if (cmd == CMD_SENSOR_READINGS_SEND) {
        // send sensor readings
        //sonarRead(false);
        //irRead(false);
        //mwReadFreq(false);
        byte payload[7];
        payload[0] = cmd;
        payload[1] = 0; // index of reading failure (zero if no failures)
        payload[2] = (int) sonarReadInches / 12;
        payload[3] = (int) sonarReadInches % 12;
        payload[4] = (int) irReadInches / 12;
        payload[5] = (int) irReadInches % 12;
        payload[6] = mwCycleCnt;
        xbeeSend(payload, sizeof(payload));
      } else if (cmd == CMD_SENSOR_SETTINGS_SEND) {
        // send sensor settings
        byte payload[19];
        payload[0] = cmd; // return the same command so the host will know what command response it's dealing with
        payload[1] = 0; // 1=failures, 0=no failures
        payload[2] = key0; // 1st IR remote access key code digit
        payload[3] = key1; // 2nd IR remote access key code digit
        payload[4] = key2; // 3rd IR remote access key code digit
        payload[5] = camResSelect; // camera resolution selection
        payload[6] = sonarTripOn; // is sonar alarm trip on
        payload[7] = irTripOn; // is IR alarm trip on
        payload[8] = mwTripOn; // is Microwave alarm trip on
        payload[9] = gateOpen; // is the Gate open
        payload[10] = (int) sonarLimitInches / 12; // feet before sonar alarm is tripped
        payload[11] = (int) sonarLimitInches % 12; // inches before sonar alarm is tripped
        payload[12] = sonarTripDelay; // delay between sonar alarm trips
        payload[13] = (int) irLimitInches / 12; // feet before IR alarm is tripped
        payload[14] = (int) irLimitInches % 12; // inches before IR alarm is tripped
        payload[15] = irTripDelay; // delay between sonar alarm trips
        payload[16] = mwLimitCycles; // cycles/second before microwave alarm is tripped
        payload[17] = mwTripDelay; // delay between sonar alarm trips
        payload[18] = alarmState; // state in which an alarm is triggered relative to each sensor
        xbeeSend(payload, sizeof(payload));
      } else if (cmd == 255) {
        byte payload[3];
        payload[0] = 255;
        payload[1] = 0; // index of reading failure (zero if no failures)
        payload[2] = irTestValue;
        xbeeSend(payload, sizeof(payload));
      }
      LEDWRITE(HIGH, HIGH, HIGH);
    }
  }
}
// Reads incomming data from the XBee.
// RX responses are treated as commands. Returns 1 when a RX response is read/processed or -1 when nothing is read.
// TX responses are processed as statuses. Returns 2 when a TX response is successfully read, 0 when failed, and 
// -1 when nothing is read.
int xbeeRead() {
  xbee.readPacket();
  //XBeeResponse xbeeResponse = XBeeResponse();
  if (xbee.getResponse().isAvailable()) {
    if (digitalRead(LED_BLUE_PIN) == HIGH && xbee.getResponse().getApiId() == RX_16_RESPONSE) {
      // create RX response object to hold the RX data
      Rx16Response rx16 = Rx16Response();
      // store the RX response in the rx16 object
      xbee.getResponse().getRx16Response(rx16);
      // Serial.println("RX RSSI: -");
      // Serial.println(rx16.getRssi());
      // uint8_t xbeeOption = rx16.getOption();
      // execute all RX data as command(s)
      // getData(0) == zero indicates the data at the supplied index does not exist
      int cmd = rx16.getData(0); // index 0=command, index 1=failures exist (value 0=false, 1=true)
      if (cmd == CMD_SERVO_CAM || cmd == CMD_SERVO_SONAR_IR || cmd == CMD_SERVO_MICROWAVE) {
        // set the servo selection and move the camera
        servoSelect = cmd;
        SERVOMOVE(rx16.getData(2));
      } else if (cmd == CMD_CHG_KEYS || cmd == CMD_SENSOR_SETTINGS_SET) {
          key0 = rx16.getData(2); // 1st IR remote access key code digit
          key1 = rx16.getData(3); // 2nd IR remote access key code digit
          key2 = rx16.getData(4); // 3rd IR remote access key code digit
          if (cmd == CMD_SENSOR_SETTINGS_SET) {
            camResSelect = rx16.getData(5); // camera resolution selection
            sonarTripOn = rx16.getData(6); // is sonar alarm trip on
            irTripOn = rx16.getData(7); // is IR alarm trip on
            mwTripOn = rx16.getData(8); // is Microwave alarm trip on
            gateOpen = rx16.getData(9); // is the Gate open
            sonarLimitInches = (rx16.getData(10) * 12) + rx16.getData(11); // total inches before sonar alarm is tripped
            sonarTripDelay = rx16.getData(12); // delay between sonar alarm trips
            irLimitInches = (rx16.getData(13) * 12) + rx16.getData(14); // total inches before IR alarm is tripped
            irTripDelay = rx16.getData(15); // delay between sonar alarm trips
            mwLimitCycles = rx16.getData(16); // cycles/second before microwave alarm is tripped
            mwTripDelay = rx16.getData(17); // delay between sonar alarm trips
            alarmState = rx16.getData(18); // state in which an alarm is triggered relative to each sensor
            // notify of settings success
            int sendSetCmd = CMD_SENSOR_SETTINGS_SEND;
            pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &sendSetCmd); 
          }
      } else {
        // all other commands can be buffered
        pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &cmd);
      }
      return 1;
    } else if (xbee.getResponse().getApiId() == TX_STATUS_RESPONSE) {
      // create TX status response object to hold the status data
      TxStatusResponse txStatus = TxStatusResponse();
      // store the TX response data in the txStatus object
      xbee.getResponse().getTxStatusResponse(txStatus);
      // get the delivery status, the fifth byte
      if (txStatus.getStatus() == SUCCESS) {
        return 2;
      } else {
        // the remote XBee did not receive our packet. is it powered on?
        return 0;
      }
    }
  }
  return -1;
}
// send a payload over xbee and wait for ack
int xbeeSend(byte payload[], int length) {
  // with Series 1 you can use either 16-bit or 64-bit addressing
  // 16-bit addressing: Enter address of remote XBee, typically the coordinator (16-bit 0x7777 in this case)
  Tx16Request tx = Tx16Request(0x7777, payload, length);
  xbee.send(tx);
  int xbeeRtn;
  // TODO : handle when read is called on another incoming RX during this operation
  while ((xbeeRtn = xbeeRead()) < 0);
  return xbeeRtn;
}
//============ Camera ============//
// takes a picture and sends the bytes over xbee in 64-byte chunks
// NOTE: Interrupts have to be disabled to prevent 
// interference with serial interrupt used by Arduino
boolean camTakeSendPic(int cmd) {
  irInterrupt(false);
  mwInterrupt(false);
  boolean hasFailures = false;
  // take picture (buffered in cam)
  unsigned long picTotalLen = camTakePic(cmd);
  // read pic data from camera and write to file in SD card
  // how many times we have to read data, with reading CAM_BUF_LEN length every time 
  unsigned long count = picTotalLen >> CAM_SHIFT_BIT;
  // the reset part of the pic data that is no longer than CAM_BUF_LEN
  int tail = picTotalLen & (CAM_BUF_LEN - 1);
  // start from 0, and then add CAM_BUF_LEN every time we read pic data
  unsigned long addr = 0;
  camData[5] = addr >> 0;
  camData[6] = addr >> 0;
  camData[7] = addr >> 0;
  camData[10] = CAM_BUF_LEN;
  // get and save count*CAM_BUF_LEN data
  int i = 0;
  for (i=0; i<count; i++) {
    CAMSENDCMD(camData);
    hasFailures = camReadXBeeSend(cmd, hasFailures, i, CAM_BUF_LEN);
    addr += CAM_BUF_LEN;
    camData[5] = addr >> 16;
    camData[6] = addr >> 8;
    camData[7] = addr;
  }
  // get reset of the pic data
  camData[10] = tail;
  CAMSENDCMD(camData);
  hasFailures = camReadXBeeSend(cmd, hasFailures, i, tail);
  irInterrupt(true);
  mwInterrupt(true);
}
// Takes a pic by sending either the QVGA or VGA command to the cam
// and returning the size of the pic buffered in the cam
unsigned long camTakePic(int cmd) {
  // turn IR board LED on for night vision. board has a built-in 
  // photo resister that will only illuminate when dark
  digitalWrite(CAM_IR_BOARD_PIN, HIGH);
  Serial1.flush();
  unsigned long picTotalLen = 0;
  CAMSENDCMD((camResSelect == CAM_QVGA ? camQVGA : camVGA));
  // get pic size response
  while (Serial1.available() < 3);
  picTotalLen |= Serial1.read() << 16;
  picTotalLen |= Serial1.read() << 8;
  picTotalLen |= Serial1.read();
  // Serial.println("Pic sz: ");
  // Serial.println(picTotalLen);
  digitalWrite(CAM_IR_BOARD_PIN, LOW);
  return picTotalLen;
}
// read the sepecific length of pic data from serial camera
// and send the read bytes over xbee
boolean camReadXBeeSend(int cmd, boolean hasFailures, int index, int toBeReadLen) {
  int readLen = 0;
  Tx16Request tx;
  short infoSegCnt = 7;
  byte payload[toBeReadLen + infoSegCnt];
  payload[0] = cmd;
  payload[1] = (unsigned char) hasFailures;
  payload[2] = (int) sonarReadInches / 12; // feet
  payload[3] = (int) sonarReadInches % 12; // inches
  payload[4] = (int) irReadInches / 12; // feet
  payload[5] = (int) irReadInches % 12; // inches
  payload[6] = mwCycleCnt;
  if (toBeReadLen > 0) {
    while (readLen < toBeReadLen) {
      if (Serial1.available() > 0) {
        payload[readLen + infoSegCnt] = Serial1.read();
        readLen++;
      }
    }
    if (readLen > 0) {
        tx = Tx16Request(0x7777, payload, sizeof(payload));
        xbee.send(tx);
        int xbeeRtn;
        // TODO : handle when read is called on another incoming RX during this operation
        while ((xbeeRtn = xbeeRead()) < 0);
        switch (xbeeRtn) {
          case 0: hasFailures = true; break;
          case 2: break;
        }
    }
  }
  return hasFailures;
}
//============ Sonar ============//
// Takes a sonar distance measurement reading for the pulse in.
// When the reading is within the specified threshold and "isTrip"
// is true the trip command will be entered into the command buffer.
// NOTE: pulsing on a digital pin seems to be more accurate than the analog method
byte sonarRead(byte isTrip) {
  digitalWrite(SONAR_TX_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(SONAR_TX_PIN, LOW);
  // pulse width representation with a scale factor of 147 uS per inch.
  long pulse = pulseIn(SONAR_RX_PIN, HIGH);
  // 147uS per inch (58uS per cm)
  sonarReadInches = pulse / 147.32;
  //long cm = sonarReadInches * 2.54;
  //int sonarFeet = (int) sonarReadInches / 12;
  //int sonarInches = (int) sonarReadInches % 12;
  
  // allocate two bytes for to hold a 10-bit analog reading
  // uint8_t payload[] = {0, 0};
  // int analogRead = analogRead(SONAR_RX_PIN);
  // payload[0] = analogRead >> 8 & 0xff;
  // payload[1] = analogRead & 0xff;
  
  // int allocation requires 4 bytes (if int can be over 255)
  //payload[0] = (byte) sonarFeet;
  //payload[1] = (byte) sonarFeet >> 8;
  //payload[2] = (byte) sonarFeet >> 16;
  //payload[3] = (byte) sonarFeet >> 24;

  // sonarFeet will never be > 25' (range of the sonar)
  // sonarInches will never be > 11" (converted to feet)
  if (isTrip && ALRMISTRIPPED(ALRM_SONAR)) {
    // buffer trip command (default takes a picture and send the data over xbee)
    pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &tripCmd);
    return 1;
  }
  return 0;
}
//============ IR Remote ============//
// Setup IR remote control to read incoming signals on interrupt
void irSetup() {
  pinMode(IR_LED_PIN, OUTPUT);
  pinMode(IR_REMOTE_PIN, INPUT);
  // initial wait is zero
  irWaitFor = millis();
  irInterrupt(true);
}
// Takes an IR distance measurement reading by sending an IR pulse via an IR LED and set on 
// "irFeet"/"irInches". When the reading is within the specified threshold and "isTrip" is 
// true the trip command will be entered into the command buffer.
byte irRead(byte isTrip) {
  // TODO : impl IR LED pulse and read to values
  irInterrupt(false);
  // one period at 38.5khZ is aproximately 26 microseconds
  // 26 microseconds * 38 is more or less 1 millisecond
  /*long usecs = 500;
  while (usecs > 0) {
    // 38 kHz is about 13 microseconds high and 13 microseconds low
   digitalWrite(IR_LED_PIN, HIGH);  // this takes about 3 microseconds to happen
   delayMicroseconds(10);         // hang out for 10 microseconds
   digitalWrite(IR_LED_PIN, LOW);   // this also takes about 3 microseconds
   delayMicroseconds(10);         // hang out for 10 microseconds
   // so 26 microseconds altogether
   usecs -= 26;
  }*/
  /*if (digitalRead(IR_REMOTE_PIN) == 1) {
      // buffer trip command (default takes a picture and send the data over xbee)
      //pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &tripCmd);
      LEDWRITE(LOW, HIGH, HIGH);
  }*/
  /*LEDWRITE(LOW, HIGH, HIGH);
      byte payload[3];
      payload[0] = 255;
      payload[1] = 0; // index of reading failure (zero if no failures)
      payload[2] = PORTDbits.RD8;
      xbeeSend(payload, sizeof(payload));
  LEDOFF();*/
  float volts = analogRead(IR_REMOTE_PIN)*0.0048828125;   // value from sensor * (5/1024) - if running 3.3.volts then change 5 to 3.3
  float distance = 65*pow(volts, -1.10);          // worked out from graph 65 = theretical distance / (1/Volts)S - luckylarry.co.uk
  if (distance > 0) {
    LEDWRITE(LOW, HIGH, HIGH);
      byte payload[3];
      payload[0] = 255;
      payload[1] = 0; // index of reading failure (zero if no failures)
      payload[2] = distance;
      xbeeSend(payload, sizeof(payload));
      LEDOFF();
  }
  if (isTrip && ALRMISTRIPPED(ALRM_IR)) {

  }
  irInterrupt(true);
}
// toggle the IR remote control interrupt
void irInterrupt(boolean on) {
  if (on) {
    // Pin 2 (INT1) for IR readings
    // priority level must be one or it will take up to a minute to return to main loop!
    //ConfigINT1(EXT_INT_PRI_1 | FALLING_EDGE_INT | EXT_INT_ENABLE);
    attachInterrupt(1, irTest, RISING);
  } else {
    //ConfigINT1(EXT_INT_PRI_1 | FALLING_EDGE_INT | EXT_INT_DISABLE);
    detachInterrupt(1);
  } 
}
void irTest() {
          //LEDWRITE(HIGH, LOW, HIGH);
  float volts = analogRead(IR_REMOTE_PIN)*0.0048828125;   // value from sensor * (5/1024) - if running 3.3.volts then change 5 to 3.3
  float distance = 65*pow(volts, -1.10);          // worked out from graph 65 = theretical distance / (1/Volts)S - luckylarry.co.uk
  //Serial.print("VOLTS: ");
  //Serial.print(volts);
  //Serial.print(" distance: ");
  //Serial.println(distance);
    //irTestValue = distance;//PORTDbits.RD8;
    //pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &irTripCmd);  
}
/*ifdef __cplusplus
extern "C" {
#endif
// Interrupt that evaluates an IR remote control key press and stores the commands in a buffer
// for execution in the main loop (pin goes LOW)
// TODO: Use of macros inside ISRs are ok, but calls to functions inside an ISR will 
// require the prolog and epilog to save/restore all registers!
void __ISR(_EXTERNAL_1_VECTOR, ipl1) IRPulseHandler(void) {
  //mINT1SetIntPriority(1);
  mINT1ClearIntFlag();
  //mINT0IntEnable(1);
*/
void irHandler() {
  // Wait for a start bit IR pulse threshold (in microseconds)
  // 12 bit = 2.400 ms indicates start
  // 15 bit = 2.940 ms indicates start
  // 20 bit = 3.840 ms indicates start
  int irPulse = pulseIn(IR_REMOTE_PIN, LOW);
  if (irPulse >= IR_PULSE_THRESHOLD_US) {
    int currKey = 0;
    byte seed = 1;
    // measure low pulse bits, convert the pulse values to binary values then to integers
    // 7 bit command followed by...
    // 5 bit device address (12 bit) or
    // 8 bit device address (15 bit) or
    // 5 bit device address and 8 bit extended (20 bit)
    for (byte i=0; i<IR_KEY_COMMAND_BITS; i++) {
      irPulse = pulseIn(IR_REMOTE_PIN, LOW);
      if (irPulse >= IR_PULSE_ONE_US) {
        // pulse is logical one
        currKey += seed;
        seed = seed * 2;
      } else if (irPulse >= IR_PULSE_ZERO_US) {
        // pulse is logical zero
        seed = seed * 2;
      } else {
        currKey = -1;
        break;
      }
    }
    //Serial.println(currKey);
    if (currKey > -1) {
      // prevent unintentional duplicate key entry by only capturing a key every 
      // IR_KEY_WAIT_MS milliseconds (unless the key is for servo movement) millis() 
      // doesn't inc in ISRs, but due to rapid pulse it will return to main long 
      // enough to inc millis()
      // setup timer for rollover handling see: http://www.arduino.cc/playground/Code/TimingRollover
      long waitCalc = SERVOISCMD(currKey) ? 0 : (long)(millis() - irWaitFor);
      // reset when no keys are pressed within XX milliseconds
      if (waitCalc <= 0 || waitCalc >= IR_KEY_WAIT_MS) {
        irWaitFor = millis();
        // reset when no keys are pressed within XX milliseconds
        boolean isReset = (waitCalc >= IR_KEY_SESSION_TIMEOUT_MS);
        if (irProceed(&currKey, &isReset)) {
          if (currKey == CMD_TOOGLE_SERVO) {
            SERVOTOGGLE();
          } else if (SERVOISCMD(currKey)) {
            // need to move the servo immediatly to get a good response rate
            SERVOMOVE(currKey);
          } else {
            // command can wait... buffer and return to main loop
            pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &currKey);
          }
        }
      }
    }
  }
}
/*ifdef __cplusplus
}
#endif*/
// Returns true when the current key should be processed.
// When a valid key combination has previously been entered and the "Input" key is pressed
// a new key code begins to be captured until all keys are present at which point they are saved 
// in the corresponding keys- only when isReset is passed as false and the "Mute" button was not pressed 
// to invoke a reset.
boolean irProceed(int* currKey, boolean* isReset) {
  boolean proceed = false;
  boolean sessionTimeout = false;
  if (*currKey == CMD_SESSION_RESET) {
    // mute pressed- reset session
    *isReset = true;
  }
  if (!*isReset) {
    int i = irKeys[0] == cmdNA ? 0 : irKeys[1] == cmdNA ? 1 : irKeys[2] == cmdNA ? 2 : -1;
    if (i <= cmdNA) {
      if (!irKeyChange && !IRISAUTH()) {
        // access denied!
        *isReset = true;
        LEDWRITE(LOW, HIGH, HIGH);
      } else if (*currKey == CMD_CHG_KEYS) {
        // changing access key code combination
        irKeyChange = true;
        *isReset = true;
        LEDWRITE(HIGH, HIGH, LOW);
      } else if (!irKeyChange) {
        // access granted!
        proceed = true;
        LEDWRITE(HIGH, LOW, HIGH);
      }
    } else if (IRISKEYNAN(*currKey)) {
      // only numeric keys are accepted until authorized
      LEDWRITE(LOW, HIGH, HIGH);
    } else {
      // entering access code keys
      irKeys[i] = *currKey;
      if (irKeyChange && i == 2) {
        // save new access key code combination
        irKeyChange = false;
        key0 = irKeys[0];
        key1 = irKeys[1];
        key2 = irKeys[2];
        LEDWRITE(HIGH, HIGH, LOW);
      } else {
        LEDWRITE(LOW, LOW, LOW);
      }
    }
  } else {
    // session timout
    irKeyChange = false;
    *isReset = true;
    sessionTimeout = true;
    LEDWRITE(LOW, HIGH, HIGH);
  }
  if (*isReset) {
    // count current key as entry for new access key entry when numeric
    irKeys[0] = sessionTimeout && *currKey >= 0 && *currKey <= 9 ? *currKey : cmdNA;
    irKeys[1] = cmdNA;
    irKeys[2] = cmdNA;
  }
  return proceed;
}
//============ Microwave ============//
// To start the sensor reading, enable pin EN (pin 9) with HIGH signal
// To make sure it's a clean HIGH signal, give small LOW signal before
void mwSetup() {
  pinMode(MW_PIN, INPUT);
  pinMode(MW_EN_PIN, OUTPUT);
  digitalWrite(MW_EN_PIN, LOW);
  delayMicroseconds(5);
  digitalWrite(MW_EN_PIN, HIGH);
  //mwInterrupt(true);
}
#include "wiring_private.h"
#include "pins_arduino.h"
// reads the cycle count of the microwave and stores it in "mwCycleCnt" (adds the trip command when "isTrip")
byte mwReadFreq(byte isTrip) {
  if (!mwTrigger) {
    return 0;
  }
  mwCycleCnt = 0;
  int mwState = 0;
  unsigned long currMillis = millis();
  unsigned long cycleMillis;
  unsigned long overheadMillis;
  while ((cycleMillis = millis()) - currMillis < (MW_MS - overheadMillis)) {
    if (MWPINREAD() != mwState) {
      mwState = !mwState;
      mwCycleCnt++;
      overheadMillis += millis() - cycleMillis;
    }
  }
  if (ALRMISTRIPPED(ALRM_MW)) {
    //long speedMM = mwCycleCnt * 30000 / 2105; // mm/second
    //long speedIN = mwCycleCnt * 30000 / 53467; // inches/second
    //Serial.print(" Speed (changes/sec): ");
    //Serial.print(mwCycleCnt);
    //Serial.print(" mm/sec: ");
    //Serial.print(speedMM);
    //Serial.print(" inches/sec: ");
    //Serial.print(speedIN);
    //Serial.print(" MPH: ");
    //Serial.println(speedIN / 17.5999999982);
    if (isTrip) {
      // buffer trip command (default takes a picture and send the data over xbee)
      //pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &tripCmd);
      LEDWRITE(HIGH, HIGH, LOW);
      byte payload[3];
      payload[0] = 255;
      payload[1] = 0; // index of reading failure (zero if no failures)
      payload[2] = mwCycleCnt;
      xbeeSend(payload, sizeof(payload));
      LEDOFF();
    }
    //mwTrigger = false;
    return 1;
  } else {
    //mwTrigger = false;
    return 0;
  }
}
// toggle the IR remote control interrupt
void mwInterrupt(boolean on) {
  if (on) {
    // Pin 7 (INT2) for readings
    // priority level must be one or it will take up to a minute to return to main loop!
    //ConfigINT2(EXT_INT_PRI_1 | RISING_EDGE_INT | EXT_INT_ENABLE);
    //attachInterrupt(2, mwHandler, RISING);
    mwTrigger = true;
  } else {
    //ConfigINT2(EXT_INT_PRI_1 | RISING_EDGE_INT | EXT_INT_DISABLE);
    //detachInterrupt(2);
    mwTrigger = false;
  }
}
/*ifdef __cplusplus
extern "C" {
#endif
// Interrupt that evaluates an IR remote control key press and stores the commands in a buffer
// for execution in the main loop (pin goes LOW)
// TODO: Use of macros inside ISRs are ok, but calls to functions inside an ISR will 
// require the prolog and epilog to save/restore all registers!
void __ISR(_EXTERNAL_2_VECTOR, ipl1) MWHandler(void) {
  //mINT2SetIntPriority(1);
  mINT2ClearIntFlag();
*/
void mwHandler() {
  if (!mwTrigger) {
    mwTrigger = true;
  }
}
/*ifdef __cplusplus
}
#endif
*/
