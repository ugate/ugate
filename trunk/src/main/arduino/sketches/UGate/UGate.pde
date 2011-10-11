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
#define NUMOFELEM(a) (sizeof(a)/sizeof(a[0]))
//======= Exception Handlers =======
#include <exceptions.h>
//======= XBee (XBee Adapter with XBee Pro 60mW Chip Antenna [~1 Mile Range]) =======
// unless you have MY on the receiving radio set to FFFF, receives will be RX16 packets
#include <XBee.h>
XBee xbee = XBee();
//======= LEDs =======
#define LED_RED_PIN 11
#define LED_GREEN_PIN 12
#define LED_BLUE_PIN 13
// Turns the LED indicator on/off (LOW is on)
#define LEDWRITE(red, green, blue) digitalWrite(LED_RED_PIN, (red)); digitalWrite(LED_GREEN_PIN, (green)); digitalWrite(LED_BLUE_PIN, (blue));
// gives any previously lit LEDs time to show before turning them off
//#define LEDOFF() if((long)(millis() - irWaitFor) >= 3){LEDWRITE(HIGH, HIGH, HIGH);}
#define LEDOFF() LEDWRITE(HIGH, HIGH, HIGH);
//======= Executing COMMANDS =======
#define CMD_SESSION_RESET 20
#define CMD_TAKE_SEND_PIC 29
#define CMD_CHG_KEYS 37
#define CMD_TOOGLE_SERVO 58
#define CMD_SERVO_CAM 103 // flag to indicate cam servo selection
#define CMD_SERVO_SONAR_IR 104 // flag to indicate sonar/IR servo selection
#define CMD_SERVO_MOICROWAVE 105 // flag to indicate microwave servo selection
int cmdNA = -1; // invalid command indicator
volatile int cmdBuffer[] = {cmdNA,cmdNA,cmdNA}; // commands waiting to be executed
byte key0 = 1, key1 = 2, key2 = 3; // default access key codes
int tripCmd = CMD_TAKE_SEND_PIC; // default trip command is to take a pic and send to host
//======= Camera (Serial CCD Electronic Brick Camera [Seed Studio]) =======
#define CAM_BUF_LEN 64
#define CAM_SHIFT_BIT 6
#define CAMSENDCMD(cmd) {int t = 0; for(t = 0; t < 11; t++) Serial1.print(cmd[t]);}
#define CAM_VGA 0
#define CAM_QVGA 1
byte camResSelect = CAM_VGA;
// commands for capturing VGA/QVGA and getting the data
byte camVGA[] = {0x55,0x41,0x52,0x54,0xCA,0x00,0x00,0x00,0x00,0x00,0x00};
byte camQVGA[] = {0x55,0x41,0x52,0x54,0xCA,0x00,0x00,0x00,0xFF,0x00,0x00};
byte camData[] = {0x55,0x41,0x52,0x54,0xC7,0x00,0x00,0x00,0x00,0x00,0x00};
//======= Sonar (XL-MaxSonar EZ3 MB1230 [~25ft Range]) =======
#define SONAR_PIN 4
unsigned long sonarLimitInches = 60; // default inches that will cause a sonar alarm trip
int sonarFeet, sonarInches, sonarTripDelay; // current or last sonar distance reading, sonar delay between alarm trips (in minutes)
int sonarTripOn = 1; // sonar alarm trip on/off
//======= IR Remote Control Receiver (TSOP38238) =======
// uno ::: IR must be on Pin 2 (INT0), Pin 3 (INT1) for hardware interrupt
// uno32 ::: IR must be on Pin 38 (INT0), Pin 2 (INT1), Pin 7 (INT2), Pin 8 (INT3), Pin 35 (INT4) for hardware interrupt
#define IR_REMOTE_PIN 2
#define IR_PULSE_THRESHOLD_US 2400 // start bit for start of command
#define IR_PULSE_ONE_US 1000 // logical one denoted by 1.2 ms
#define IR_PULSE_ZERO_US 400 // logical zero denoted by 0.6 ms
#define IR_KEY_WAIT_MS 100 // prevent unintentional duplicate key entry by only capturing a key every 
#define IR_KEY_SESSION_TIMEOUT_MS 30000 // reset when no keys are pressed within 30 seconds
#define IR_KEY_COMMAND_BITS 7 // Sony has a 7 bit command
#define IRISAUTH() (irKeys[0] == key0 && irKeys[1] == key1 && irKeys[2] == key2)
#define IRISKEYNAN(key) ((key) < 0 || (key) > 9) // indicates if numeric keys have been pressed
volatile unsigned long irWaitFor; // millis period place-holder to indicate how long its been since last key pulse (used to prevent duplicate keys when keys are briefly held down) 
volatile byte irKeyChange = false; // flag that indicates when the access key codes are being changed via IR remote
volatile int irKeys[] = {cmdNA,cmdNA,cmdNA}; // access key codes entered by the user - reset when the IR remote session expires
unsigned long irLimitInches = 60; // default inches that will cause a sonar alarm trip
int irFeet, irInches, irTripDelay; // current or last IR distance reading, IR delay between alarm trips (in minutes)
int irTripOn = 1; // IR alarm trip on/off
//======= Microwave (X-Band 10.525 GHz Microwave Motion/Speed Sensor [~30ft Range]) =======
#define MW_EN_PIN 6
#define MW_PIN 7
unsigned int mwLimitCycles = 25; // default num of cycles/sec for alarm trip
volatile byte mwTrigger; // flag to indicate if the microwave read should be performed - INTERRUPT ONLY!
int mwCycleCnt, mwTripDelay; // current or last microwave cycle count reading, microwave delay between alarm trips (in minutes)
int mwTripOn = 1; // microwave alarm trip on/off
//======= Servos =======
#include <Servo.h>
Servo servoTiltSonar;
Servo servoPanSonar;
Servo servoTiltCam;
Servo servoPanCam;
Servo servoPanMw;
volatile byte servoSelect = CMD_SERVO_CAM; // current servo selection
#define SERVO_CAM_PAN_PIN 32
#define SERVO_CAM_TILT_PIN 33
#define SERVO_SONAR_PAN_PIN 6
#define SERVO_SONAR_TILT_PIN 7
#define SERVO_MICROWAVE_PAN_PIN 31
#define SERVO_MIN_ANGLE 0
#define SERVO_MAX_ANGLE 179
#define SERVO_INC_ANGLE 10
#define SERVOISCMD(cmd) ((cmd) == 16 || (cmd) == 17 || (cmd) == 18 || (cmd) == 19) // is cmd for a servo movement
#define SERVOTOGGLE() servoSelect = servoSelect == CMD_SERVO_SONAR_IR ? CMD_SERVO_CAM : CMD_SERVO_SONAR_IR; // toggle between cam and sonar/IR servos- excludes microwave
#define SERVODETACH() \
if (servoTiltCam.attached()) { \
  servoTiltCam.detach(); \
} \
if (servoPanCam.attached()) { \
  servoPanCam.detach(); \
} \
if (servoTiltSonar.attached()) { \
  servoTiltSonar.detach(); \
} \
if (servoPanSonar.attached()) { \
  servoPanSonar.detach(); \
} \
if (servoPanMw.attached()) { \
  servoPanMw.detach(); \
}
#define SERVOMOVE(cmd) \
if (servoSelect == CMD_SERVO_SONAR_IR) { \
  if ((cmd) == 16 || (cmd) == 17) { \
    servoTiltSonar.attach(SERVO_SONAR_TILT_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoTiltSonar.write(servoTiltSonar.read() + ((cmd) == 16 ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } else if ((cmd) == 18 || (cmd) == 19) { \
    servoPanSonar.attach(SERVO_SONAR_PAN_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoPanSonar.write(servoPanSonar.read() + ((cmd) == 18 ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } \
} else if (servoSelect == CMD_SERVO_MICROWAVE) { \
  if ((cmd) == 18 || (cmd) == 19) { \
    servoPanMw.attach(SERVO_MICROWAVE_PAN_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoPanMw.write(servoPanMw.read() + ((cmd) == 18 ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } \
} else { \
  if ((cmd) == 16 || (cmd) == 17) { \
    servoTiltCam.attach(SERVO_CAM_TILT_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoTiltCam.write(servoTiltCam.read() + ((cmd) == 16 ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } else if ((cmd) == 18 || (cmd) == 19) { \
    servoPanCam.attach(SERVO_CAM_PAN_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoPanCam.write(servoPanCam.read() + ((cmd) == 18 ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } \
}
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
  irSetup();
  mwSetup();
}
void loop() {
  execBufferCmds();
  xbeeRead();
  sonarRead(true);
  irRead(true);
  mwReadFreq(true);
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
// Executes all of the buffered commands in the order they are recieved
void execBufferCmds() {
  LEDOFF();
  int cmd;
  int numElem = NUMOFELEM(cmdBuffer);
  for (short i=0; i<numElem; i++) {
    cmd = pressBuffer((int*) &cmdBuffer, numElem, (int*) &cmdNA);
    if (cmd > 0 && cmd != cmdNA) {
      LEDWRITE(HIGH, HIGH, LOW);
      if (cmd == CMD_TAKE_SEND_PIC) {
        // take/send pic in QVGA (320x240) or VGA (640x480) over xbee using param1=sonarFeet, param2=sonarInches
        camTakeSendPic(cmd);
      } else if (SERVOISCMD(cmd)) {
        // cmds: 16-19 - move the camera, sonar/IR armature, or microwave (which one depends on servoSelect)
        SERVOMOVE(cmd);
      } else if (cmd == CMD_SESSION_RESET) {
        // reset session
        irKeys[0] = irKeys[1] = irKeys[2] = cmdNA;
      } else if (cmd == CMD_TOOGLE_SERVO) {
        // toggle between camera and sonar/IR armature servos
        SERVOTOGGLE();
      } else if (cmd == 59) {
        // TODO : need to impl gate open/close
      } else if (cmd == 100) {
        // send access code
        byte payload[5];
        payload[0] = cmd;
        payload[1] = 0; // failure flag
        payload[2] = key0;
        payload[3] = key1;
        payload[4] = key2;
        xbeeSend(payload);
      } else if (cmd == 101) {
        // TODO : send "cam follow sensor angles on trip"
      } else if (cmd == 106) {
        // send the camera resolution
        byte payload[3];
        payload[0] = cmd;
        payload[1] = 0; // failure flag
        payload[2] = camResSelect;
        xbeeSend(payload);
      } else if (cmd == 108) {
        // send sensor readings
        sonarRead(false);
        irRead(false);
        mwReadFreq(false);
        byte payload[7];
        payload[0] = cmd;
        payload[1] = 0; // failure flag
        payload[2] = sonarFeet;
        payload[3] = sonarInches;
        payload[4] = irFeet;
        payload[5] = irInches;
        payload[6] = mwCycleCnt;
        xbeeSend(payload);
      } else if (cmd == 109) {
        // send sensor settings
        byte payload[10];
        payload[0] = cmd;
        payload[1] = 0; // failure flag
        payload[2] = (int) sonarLimitInches / 12; // feet
        payload[3] = (int) sonarLimitInches % 12; // inches
        payload[4] = sonarTripDelay;
        payload[5] = (int) irLimitInches / 12; // feet
        payload[6] = (int) irLimitInches % 12; // inches
        payload[7] = irTripDelay;
        payload[8] = mwLimitCycles;
        payload[9] = mwTripDelay;
        xbeeSend(payload);
      } else if (cmd == 113) {
        // sends the on/off flag for the alarm trips
        byte payload[5];
        payload[0] = cmd;
        payload[1] = 0; // failure flag
        payload[2] = sonarTripOn;
        payload[3] = irTripOn;
        payload[4] = mwTripOn;
        xbeeSend(payload);
      } else if (cmd == 115) {
        // TODO : send the current state of the gate (0=open, 1=closed);
      } else if (cmd == 117) {
        // TODO : send the current multi-alarm trip state (what alarm trip combination will trigger a trip)
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
      int cmd = rx16.getData(0); // 0=command, 1=failures exist (value 0=false, 1=true)
      if (cmd == CMD_CHG_KEYS) {
          key0 = rx16.getData(2);
          key1 = rx16.getData(3);
          key2 = rx16.getData(4);
      } else if (cmd == 102) {
        // TODO : set "cam follow sensor angles on trip"
      } else if (cmd == CMD_SERVO_CAM || cmd == CMD_SERVO_SONAR_IR || cmd == CMD_SERVO_MICROWAVE) {
        // set the servo selection and move the camera
        servoSelect = cmd;
        SERVOMOVE(rx16.getData(2));
      } else if (cmd == 107) {
        // set the camera resolution
        camResSelect = rx16.getData(2);
      } else if (cmd == 110) {
        // set sensor settings
        sonarLimitInches = (rx16.getData(2) * 12) + rx16.getData(3);
        sonarTripDelay = rx16.getData(4);
        irLimitInches = (rx16.getData(5) * 12) + rx16.getData(6);
        irTripDelay = rx16.getData(7);
        mwLimitCycles = rx16.getData(8);
        mwTripDelay = rx16.getData(9);
      } else if (cmd == 114) {
        // sets the on/off flag for the alarm trips
        sonarTripOn = rx16.getData(2);
        irTripOn = rx16.getData(3);
        mwTripOn = rx16.getData(4);
        xbeeSend(payload);
      } else if (cmd == 116) {
        // TODO : set the current state of the gate (0=open, 1=closed);
      } else if (cmd == 118) {
        // TODO : set the current multi-alarm trip state (what alarm trip combination will trigger a trip)
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
int xbeeSend(byte payload[]) {
  // with Series 1 you can use either 16-bit or 64-bit addressing
  // 16-bit addressing: Enter address of remote XBee, typically the coordinator (16-bit 0x7777 in this case)
  Tx16Request tx = Tx16Request(0x7777, payload, sizeof(payload));
  xbee.send(tx);
  int xbeeRtn;
  // TODO : handle when read is called on another incoming RX during this operation
  while ((xbeeRtn = xbeeRead()) < 0);
  return xbeeRtn;
}
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
  payload[2] = sonarFeet;
  payload[3] = sonarInches;
  payload[4] = mwCycleCnt;
  payload[5] = 0; // reserved for possible IR or PIR reading
  payload[6] = 0; // reserved for possible IR or PIR reading
  if (toBeReadLen > 0) {
    while (readLen < toBeReadLen) {
      if (Serial1.available() > 0) {
        // first 3 bytes of payload are sonarFeet, sonarInches, number of bytes
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
  // pulse width representation with a scale factor of 147 uS per inch.
  long pulse = pulseIn(SONAR_PIN, HIGH);
  // 147uS per inch (58uS per cm)
  long totalInches = pulse / 147.32;
  // change inches to centimetres
  long cm = totalInches * 2.54;
  sonarFeet = (int) totalInches / 12;
  sonarInches = (int) totalInches % 12;
  // allocate two bytes for to hold a 10-bit analog reading
  // uint8_t payload[] = {0, 0};
  // int analogRead = analogRead(SONAR_PIN);
  // payload[0] = analogRead >> 8 & 0xff;
  // payload[1] = analogRead & 0xff;
  
  // int allocation requires 4 bytes (if int can be over 255)
  //payload[0] = (byte) sonarFeet;
  //payload[1] = (byte) sonarFeet >> 8;
  //payload[2] = (byte) sonarFeet >> 16;
  //payload[3] = (byte) sonarFeet >> 24;

  // sonarFeet will never be > 25' (range of the sonar)
  // sonarInches will never be > 11" (converted to feet)
  if (isTrip && sonarTripOn && totalInches >= sonarLimitInches) {
    // buffer trip command (default takes a picture and send the data over xbee)
    pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &tripCmd);
    return 1;
  }
  return 0;
}
//============ IR Remote ============//
// Setup IR remote control to read incoming signals on interrupt
void irSetup() {
  pinMode(IR_REMOTE_PIN, INPUT);
  // setup timer for rollover handling see: http://www.arduino.cc/playground/Code/TimingRollover
  // initial wait is zero
  irWaitFor = millis();
  irInterrupt(true);
}
// toggle the IR remote control interrupt
void irInterrupt(boolean on) {
  if (on) {
    // until attachinterrupt is impl need to manually setup Pin 2 (INT1) for IR readings
    // priority level must be one or it will take up to a minute to return to main loop!
    ConfigINT1(EXT_INT_PRI_1 | FALLING_EDGE_INT | EXT_INT_ENABLE);
    //attachInterrupt(1, irCaptureKey, LOW);
  } else {
    ConfigINT1(EXT_INT_PRI_1 | FALLING_EDGE_INT | EXT_INT_DISABLE);
    //detachInterrupt(1);
  } 
}
// Takes an IR distance measurement reading by sending an IR pulse via an IR LED and set on 
// "irFeet"/"irInches". When the reading is within the specified threshold and "isTrip" is 
// true the trip command will be entered into the command buffer.
byte irRead(byte isTrip) {
  // TODO : impl IR LED pulse and read to values
  if (irTripOn) {
    
  }
}
#ifdef __cplusplus
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
#ifdef __cplusplus
}
#endif
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
  mwInterrupt(true);
}
// reads the cycle count of the microwave and stores it in "mwCycleCnt" (adds the trip command when "isTrip")
byte mwReadFreq(byte isTrip) {
  if (!mwTrigger) {
    return 0;
  }
  mwCycleCnt = 0;
  int mwState = 0;
  unsigned long currMillis = millis();
  while (millis() - currMillis < 500) {
    if (digitalRead(MW_PIN) != mwState) {
      mwState = -(mwState - 1);  //changes current_state from 0 to 1, and vice-versa
      mwCycleCnt++;
    }
  }
  if (mwCycleCnt >= mwLimitCycles) {
    //long speedMM = mwChgCnt * 30000 / 2105; // mm/second
    //long speedIN = mwChgCnt * 30000 / 53467; // inches/second
    //Serial.print(" Speed (changes/sec): ");
    //Serial.print(mwChgCnt);
    //Serial.print(" mm/sec: ");
    //Serial.print(speedMM);
    //Serial.print(" inches/sec: ");
    //Serial.print(speedIN);
    //Serial.print(" MPH: ");
    //Serial.println(speedIN / 17.5999999982);
    if (isTrip && mwTripOn) {
      // buffer trip command (default takes a picture and send the data over xbee)
      pressBuffer((int*) cmdBuffer, NUMOFELEM(cmdBuffer), &tripCmd);
    }
    mwTrigger = false;
    return 1;
  } else {
    mwTrigger = false;
    return 0;
  }
}
// toggle the IR remote control interrupt
void mwInterrupt(boolean on) {
  if (on) {
    // until attachinterrupt is impl need to manually setup Pin 2 (INT1) for IR readings
    // priority level must be one or it will take up to a minute to return to main loop!
    ConfigINT2(EXT_INT_PRI_1 | RISING_EDGE_INT | EXT_INT_ENABLE);
    //attachInterrupt(1, irCaptureKey, LOW);
  } else {
    ConfigINT2(EXT_INT_PRI_1 | RISING_EDGE_INT | EXT_INT_DISABLE);
    //detachInterrupt(1);
  } 
}
#ifdef __cplusplus
extern "C" {
#endif
// Interrupt that evaluates an IR remote control key press and stores the commands in a buffer
// for execution in the main loop (pin goes LOW)
// TODO: Use of macros inside ISRs are ok, but calls to functions inside an ISR will 
// require the prolog and epilog to save/restore all registers!
void __ISR(_EXTERNAL_2_VECTOR, ipl1) MWHandler(void) {
  //mINT2SetIntPriority(1);
  mINT2ClearIntFlag();
  if (!mwTrigger) {
    mwTrigger = true;
  }
}
#ifdef __cplusplus
}
#endif
