/*
File : UGate.pde
Author : Will Hoover
Version : v1.0, 07-28-2011

Local Devices:
- Arduino Uno
- XL-MaxSonar EZ3 MB1230 (~25ft Range)
- XBee Adapter with XBee Pro 60mW Chip Antenna (~1 Mile Range)
- Serial CCD Electronic Brick Camera (Seed Studio)
- TSOP382 38 kHz IR Reciever
- HD44760 based LCD 20x4 chars (optional)
- Ultra-Bright Tri-Color RGB 5mm LED (EBay or RadioShack)

Remote Devices:
- XBee Adaptor/USB to TTL UART 6PIN CP2102 with XBee Pro 60mW Chip Antenna (~1 Mile Range to PC Interface)
- GE 24944 Universal Remote

Description:
Sonar is constantly checking its pulse for a threshold that when surpassed will initiate an
on-board CCD camera to take a picture and start streaming the JPEG compressed data over XBee 
until the EOF (0xFF, 0xD9).
*/
// Need to have soft serial for XBee so we use a lib from: http://arduiniana.org/libraries/newsoftserial/
#include <NewSoftSerial.h>
//#define delayWithInterrupts(milliseconds) {for (int i=0, i=milliseconds; i++) {delayMicroseconds(1000);}}
//======= Executing COMMANDS =======
int CMD_QVGA_ID = 49; // ASCII 49 is sent as character 1
int CMD_VGA_ID = 50; // ASCII 50 is sent as character 2
//======= Camera (Serial CCD Electronic Brick Camera [Seed Studio]) =======
#define PIC_BUF_LEN 64
#define SHIFT_BIT 6
#define sendCamCmd(cmd) {int t = 0; for(t = 0; t < 11; t++) Serial.print(cmd[t]);}
// commands for capturing VGA/QVGA and getting the data
unsigned char cmdVGA[] = {0x55,0x41,0x52,0x54,0xCA,0x00,0x00,0x00,0x00,0x00,0x00};
unsigned char cmdQVGA[] = {0x55,0x41,0x52,0x54,0xCA,0x00,0x00,0x00,0xFF,0x00,0x00};
unsigned char cmdGetDat[] = {0x55,0x41,0x52,0x54,0xC7,0x00,0x00,0x00,0x00,0x00,0x00};
//======= XBee (XBee Adapter with XBee Pro 60mW Chip Antenna [~1 Mile Range]) =======
// XBee Shield can be switched using onboard SPDT switch UART pins (D0, D1) 
// or any digital pins on arduino- which frees up the Serial port (D2, D3 default)
// unless you have MY on the receiving radio set to FFFF, this will be received as a RX16 packet
#include <XBee.h>
NewSoftSerial xBeeSerial = NewSoftSerial(5, 6);
XBee xbee = XBee();
//======= MaxSonar (XL-MaxSonar EZ3 MB1230 [~25ft Range]) =======
// pulsing on a digital pin seems to be more accurate than the analog method
const uint8_t sonarPin = 4;
int feet, inches, sonarCnt, sonarBetweenLimitsCnt;
//======= IR Remote Control Receiver (TSOP38238) =======
#include <EEPROM.h>
const uint8_t ledRedPin = 11;
const uint8_t ledGreenPin = 12;
const uint8_t ledBluePin = 13;
const uint8_t irRemotePin = 2;
int no_cmd = -1;
volatile unsigned long irWaitForMillis;
volatile byte irKeyChange = false;
volatile int irKeys[] = {no_cmd,no_cmd,no_cmd};
volatile int cmdBuffer[] = {no_cmd,no_cmd,no_cmd};
//======= Servos =======
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

IMPORTANT:

NSS and Servo lib are not compatable, but we can use a lib from: http://arduiniana.org/libraries/pwmservo/
It only supports only 2 servos and this project needs 4 servos. Instead of using all these workarounds (including NSS itself which has it's own issues),
The uno code is being branched in favor of the chipKIT uno32 wich costs the same as the uno, but provides 2 UARTs (and a LOT more features/speed).
Check trunk for uno32 related code. 

$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/
#include <Servo.h>  
Servo camTiltServo;
Servo camPanServo;
//======= LCD =======
// http://www.adafruit.com/blog/2010/03/15/arduino-tutorial-connecting-a-parallel-lcd/
// http://icontexto.com/charactercreator/
//-#include <LiquidCrystal.h>
// initialize the library with the numbers of the interface pins
//-LiquidCrystal lcd(11, 10, 9, 8, 7, 6);
//LiquidCrystal lcd(12, 11, 5, 4, 3, 2); // arduino.cc wiring, xrossing
//LiquidCrystal lcd(6, 7, 8, 9, 10, 11); // ladyada wiring, straight

void setup() {
  // cam over hard serial only 1 soft serial can be active at a time- need xbee + cam active!
  Serial.begin(115200);
  // setup IR Universal Remote
  irSetup();
  //-setupLCD();
  // xbee over soft serial
  xbee.setNss(xBeeSerial);
  // unsuccessful attempts are automatically retried at host computer
  // tested at 100% success rate for baud rate 9600 (avg VGA 20 sec)
  // tested at ~100% success rate for baud rate 19200 (avg VGA 12 sec/avg QVGA 3 sec)
  // tested at ~80% success rate for baud rate 38400 (avg VGA 7 sec/avg QVGA <1 sec)
  xbee.begin(19200);
  camTiltServo.attach(10);
}
void loop() {
  xbeeRead();
  sonarRead();
  execBufferCmds();
}
// Removes/Returns the first command in the buffer and presses the specified command to the end of the buffer 
int pressBuffer(int buffer[], int* press) {
  int cmd = *(buffer + 0);
  int sz = (sizeof(int)*(sizeof(buffer)));
  memmove(buffer, buffer + 1, sz);
  memcpy(buffer + (sz - 2), press, sizeof(int));
  return cmd;
}
// Executes all of the buffered commands in the order they are recieved
void execBufferCmds() {
  ledWrite(HIGH, HIGH, HIGH);
  int cmd;
  int sz = (sizeof(int)*(sizeof(cmdBuffer)));
  for (short i=0; i<sz; i++) {
    cmd = pressBuffer((int*) &cmdBuffer, &no_cmd);
    if (cmd > 0 && cmd != no_cmd) {
      //-lcd.setCursor(0, 1);
      //-lcd.print("Exec CMD: ");
      //-lcd.print(cmdCode);
      //delay(3000);
      ledWrite(HIGH, HIGH, LOW);
      // take/send pic in QVGA (320x240) or VGA (640x480) over xbee using param1=feet, param2=inches
      if (cmd == CMD_QVGA_ID) {
        camTakeSendPic(CMD_QVGA_ID);
      } else if (cmd == CMD_VGA_ID || cmd == 29) {
        camTakeSendPic(CMD_VGA_ID);
      } else if (cmd == 17) {
        //camTiltServo.attach(10);
        camTiltServo.write(camTiltServo.read() - 10);
        //camTiltServo.detach();
      } else if (cmd == 16) {
        //camTiltServo.attach(10);
        camTiltServo.write(camTiltServo.read() + 10);
        //camTiltServo.detach();
      }
      ledWrite(HIGH, HIGH, HIGH);
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
    if (digitalRead(ledBluePin) == LOW && xbee.getResponse().getApiId() == RX_16_RESPONSE) {
      // create RX response object to hold the RX data
      Rx16Response rx16 = Rx16Response();
      // store the RX response in the rx16 object
      xbee.getResponse().getRx16Response(rx16);
      //-lcd.clear();
      //-lcd.print("RX RSSI: -");
      //-lcd.print((int) rx16.getRssi());
      // uint8_t xbeeOption = rx16.getOption();
      // execute all RX data as command(s)
      // getData(0) == zero indicates the data at the supplied index does not exist
      int cmd = rx16.getData(0);
      pressBuffer((int*)cmdBuffer, &cmd);
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
// takes a picture and sends the bytes over xbee in 64-byte chunks
// NOTE: IR remote interrupt has to be disabled to prevent interference with interrupt
// used by Arduino to handle serial communications
boolean camTakeSendPic(int cmd) {
  irInterrupt(false);
  boolean hasFailures = false;
  // take picture (buffered in cam)
  unsigned long picTotalLen = camTakePic(cmd);
  // read pic data from camera and write to file in SD card
  // how many times we have to read data, with reading PIC_BUF_LEN length every time 
  unsigned long count = picTotalLen >> SHIFT_BIT;
  // the reset part of the pic data that is no longer than PIC_BUF_LEN
  int tail = picTotalLen & (PIC_BUF_LEN - 1);
  // start from 0, and then add PIC_BUF_LEN every time we read pic data
  unsigned long addr = 0;
  cmdGetDat[5] = addr >> 0;
  cmdGetDat[6] = addr >> 0;
  cmdGetDat[7] = addr >> 0;
  cmdGetDat[10] = PIC_BUF_LEN;
  // get and save count*PIC_BUF_LEN data
  int i = 0;
  for (i=0; i<count; i++) {
    sendCamCmd(cmdGetDat);
    hasFailures = camReadXBeeSend(cmd, hasFailures, i, PIC_BUF_LEN);
    addr += PIC_BUF_LEN;
    cmdGetDat[5] = addr >> 16;
    cmdGetDat[6] = addr >> 8;
    cmdGetDat[7] = addr;
  }
  // get reset of the pic data
  cmdGetDat[10] = tail;
  sendCamCmd(cmdGetDat);
  hasFailures = camReadXBeeSend(cmd, hasFailures, i, tail);
  //-lcd.setCursor(0, 2);
  //-lcd.print("Pic TX Complete!!!");
  //-delay(5000);
  irInterrupt(true);
}
// Takes a pic by sending either the QVGA or VGA command to the cam
// and returning the size of the pic buffered in the cam
unsigned long camTakePic(int cmd) {
  //-lcd.clear();
  //-lcd.print("Taking PIC...");
  Serial.flush();
  unsigned long picTotalLen = 0;
  sendCamCmd((cmd == CMD_QVGA_ID ? cmdQVGA : cmdVGA));
  // get pic size response
  while (Serial.available() < 3);
  picTotalLen |= Serial.read() << 16;
  picTotalLen |= Serial.read() << 8;
  picTotalLen |= Serial.read();
  //-lcd.clear();
  //-lcd.print("Pic sz: ");
  //-lcd.print(picTotalLen);
  //~Serial.println("Pic sz: ");
  //~Serial.println(picTotalLen);
  return picTotalLen;
}
// read the sepecific length of pic data from serial camera
// and send the read bytes over xbee
boolean camReadXBeeSend(int cmd, boolean hasFailures, int index, int toBeReadLen) {
  int readLen = 0;
  Tx16Request tx;
  short infoSegCnt = 4;
  byte payload[toBeReadLen + infoSegCnt];
  payload[0] = cmd;
  payload[1] = (unsigned char) hasFailures;
  payload[2] = feet;
  payload[3] = inches;
  if (toBeReadLen > 0) {
    //-lcd.setCursor(0, 1);
    //-lcd.print("Pic Chunk: ");
    //-lcd.print(index + 1);
    while (readLen < toBeReadLen) {
      if (Serial.available() > 0) {
        // first 3 bytes of payload are feet, inches, number of bytes
        payload[readLen + infoSegCnt] = Serial.read();
        readLen++;
      } else {
        //-lcd.print("Wait CAM Avail");
      }
    }
    if (readLen > 0) {
        // with Series 1 you can use either 16-bit or 64-bit addressing
        // 16-bit addressing: Enter address of remote XBee, typically the coordinator (16-bit 0x7777 in this case)
        tx = Tx16Request(0x7777, payload, sizeof(payload));
        xbee.send(tx);
        int xbeeRtn;
        // TODO : handle when read is called on another incoming RX during this operation
        while ((xbeeRtn = xbeeRead()) < 0) {
          //-lcd.setCursor(0, 2);
          //-lcd.print("ACK WAITING: ");
          //-lcd.print(readLen);
        }
        //-lcd.setCursor(0, 2);
        switch (xbeeRtn) {
          case 0: //-lcd.print("ACK FAILED:  ");
                  hasFailures = true; break;
          case 2: //-lcd.print("ACK RECEIVED: ");
                  break;
        }
        //-lcd.print(readLen);
    }
  }
  //-lcd.setCursor(0, 3);
  //-lcd.print("Failed Chunks? ");
  //-lcd.print((hasFailures ? "YES" : "NO!"));
  return hasFailures;
}
// Used to read in the pulse that is being sent by the MaxSonar device.
// When the reading is within the specified threshold a VGA pic will be
// taken the bytes which will be sent over XBee in 64-byte chunks. 
void sonarRead() {
  // pulse width representation with a scale factor of 147 uS per inch.
  long pulse = pulseIn(sonarPin, HIGH);
  // 147uS per inch (58uS per cm)
  long totalInches = pulse / 147.32;
  // change inches to centimetres
  long cm = totalInches * 2.54;
  feet = (int) totalInches / 12;
  inches = (int) totalInches % 12;
  writeSonarLog(pulse, cm, totalInches);
  // allocate two bytes for to hold a 10-bit analog reading
  // uint8_t payload[] = { 0, 0 };
  // pin5 = analogRead(5);
  // payload[0] = pin5 >> 8 & 0xff;
  // payload[1] = pin5 & 0xff;
  
  // int allocation requires 4 bytes (if int can be over 255)
  //payload[0] = (byte) feet;
  //payload[1] = (byte) feet >> 8;
  //payload[2] = (byte) feet >> 16;
  //payload[3] = (byte) feet >> 24;

  // feet will never be > 25' (range of the sonar)
  // inches will never be > 11" (converted to feet)
  if (feet > 1 && feet < 8) {
    // buffer command to take a picture and send the data over xbee
    pressBuffer((int*)cmdBuffer, &CMD_VGA_ID);
  }
}
//============ IR Remote ============//
// Setup IR remote control. If the access key code is not in EEPROM it will be defaulted to 0,1,2 
// (1,2,3 keypad equivalent).
void irSetup() {
  // IR must be on pin 2 (interrupt 0) or pin 3 (interrupt 1) for hardware interrupt
  pinMode(sonarPin, INPUT);
  // RGB LED LOW is on
  ledWrite(HIGH, HIGH, HIGH);
  pinMode(ledRedPin, OUTPUT);
  pinMode(ledBluePin, OUTPUT);
  pinMode(ledGreenPin, OUTPUT);
  // setup timer for rollover handling see: http://www.arduino.cc/playground/Code/TimingRollover
  // initial wait is zero
  irWaitForMillis = millis() + 0;
  // if any of the keys are non-numeric set them to the 
  // default access keys: 0 (1), 1 (2), 2 (3)
  unsigned int key0 = EEPROM.read(0);
  unsigned int key1 = EEPROM.read(1);
  unsigned int key2 = EEPROM.read(2);
  // EEPROM default values will be 255 (max byte)
  if (key0 < 0 || key0 > 9 || key1 < 0 || key1 > 9 || key2 < 0 || key2 > 9) {
    EEPROM.write(0, 0);
    EEPROM.write(1, 1);
    EEPROM.write(2, 2);
  }
  irInterrupt(true);
}
// toggle the IR remote control interrupt
void irInterrupt(boolean on) {
  if (on) {
    attachInterrupt(0, irCaptureKey, LOW);
  } else {
    detachInterrupt(0);
  } 
}
// Interrupt that evaluates an IR remote control key press and stores the commands in a buffer
// for execution in the main loop (pin goes LOW)
void irCaptureKey() {
  ledWrite(HIGH, HIGH, HIGH);
  int currKey = irReadKey();
  if (currKey > -1) {
    // prevent unintentional duplicate key entry by only capturing a key every 100 ms (unless the key is for servo movement)
    long waitCalc = currKey >= 16 && currKey <= 19 ? 0 : (long)(millis() - irWaitForMillis);
    if (waitCalc <= 0 || waitCalc >= 100) {
      irWaitForMillis = millis();
      // reset when no keys are pressed within 30 seconds
      boolean isReset = (waitCalc >= 30000);
      if (irProceed(&currKey, &isReset)) {
        // add the key to the command buffer
        pressBuffer((int*)cmdBuffer, &currKey);
      }
    }
  }
}
// Returns true when the key code combination matches the one in EEPROM
boolean irIsAuth() {
  return irKeys[0] == EEPROM.read(0) && irKeys[1] == EEPROM.read(1) && irKeys[2] == EEPROM.read(2);
}
// Returns true when the current key should be processed.
// When a valid key combination has previously been entered and the "Input" [298] key is pressed
// a new key code begins to be captured until all keys are present at which point they are saved 
// in EEPROM- only when isReset is passed as false and the "Mute" [148] button was not pressed 
// to invoke a reset.
boolean irProceed(int* currKey, boolean* isReset) {
  boolean proceed = false;
  if (*currKey == 20) {
    // mute pressed- reset session
    *isReset = true;
  }
  if (!*isReset) {
    int i = irKeys[0] == no_cmd ? 0 : irKeys[1] == no_cmd ? 1 : irKeys[2] == no_cmd ? 2 : -1;
    if (i <= no_cmd) {
      if (!irKeyChange && !irIsAuth()) {
        // access denied!
        *isReset = true;
        ledWrite(LOW, HIGH, HIGH);
      } else if (*currKey == 37) {
        // changing access key code combination
        irKeyChange = true;
        *isReset = true;
        ledWrite(HIGH, HIGH, LOW);
      } else if (!irKeyChange) {
        // access granted!
        proceed = true;
        ledWrite(HIGH, LOW, HIGH);
      }
    } else {
      ledWrite(HIGH, LOW, HIGH);
      // entering access code keys
      irKeys[i] = *currKey;
      if (irKeyChange && i == 2) {
        // save new access key code combination
        irKeyChange = false;
        EEPROM.write(0, irKeys[0]);
        EEPROM.write(1, irKeys[1]);
        EEPROM.write(2, irKeys[2]);
      }
    }
  } else {
    // session timout
    irKeyChange = false;
    *isReset = true;
    ledWrite(LOW, HIGH, HIGH);
  }
  if (*isReset) {
    // count current key as entry for new access key entry when numeric
    irKeys[0] = *currKey < 0 || *currKey > 9 ? no_cmd : *currKey;
    irKeys[1] = no_cmd;
    irKeys[2] = no_cmd;
  }
  return proceed;
}
// Reads the IR Remote Controls pulse and returns the 
// integer value of the key that was pressed (-1 when invalid)
int irReadKey() {
  // wait for a start bit threshold (in microseconds)
  int pulse = pulseIn(irRemotePin, LOW);
  // 12 bit = 2.400 ms indicates start
  // 15 bit = 2.940 ms indicates start
  // 20 bit = 3.840 ms indicates start
  if (pulse < 2400) {
    return -1;
  }
  // measure low pulse bits, convert the pulse values to binary values then to integers
  int keyValue = 0;
  byte seed = 1;
  // 7 bit command followed by...
  // 5 bit device address (12 bit) or
  // 8 bit device address (15 bit) or
  // 5 bit device address and 8 bit extended (20 bit)
  for (byte i=0; i<7; i++) {
    // measure low pulse bits
    pulse = pulseIn(irRemotePin, LOW);
    if (pulse > 1000) {
      // logical one denoted by 1.2 ms
      keyValue += seed;
      seed = seed * 2;
    } else if (pulse > 400) {
      // logical zero denoted by 0.6 ms
      seed = seed * 2;
    } else {
      // invalid value
      return -1;
    }
  }
  return keyValue;
}
//============ Utilities ============//
// Writes an integer to EEPROM (integers take two address locations)
void EEPROM_writeint(int address, int value) {
  EEPROM.write(address, highByte(value));
  EEPROM.write(address + 1, lowByte(value));
}
// Reads an integer from EEPROM (integers take two address locations)
unsigned int EEPROM_readint(int address) {
  unsigned int w = word(EEPROM.read(address), EEPROM.read(address + 1));
  return w;
}
// Turns the LED indicator on/off (LOW is on)
void ledWrite(int red, int green, int blue) {
  digitalWrite(ledRedPin, red);
  digitalWrite(ledGreenPin, green);
  digitalWrite(ledBluePin, blue);
}
// Sets up an LCD for debugging
//-void setupLCD() {
  // Initialize 20x4 LCD Display
  //-lcd.begin(20, 4);
  //-lcd.print("Initializing...");
//-}
// Writes the MaxSonar device data to the LCD and Serial
void writeSonarLog(long pulse, long cm, long totalInches) {
  //-lcd.clear();
  //-lcd.print(pulse);
  //-lcd.print(" pulse");
  //-lcd.setCursor(0, 1);
  //-lcd.print(cm);
  //-lcd.print(" cm");
  //-lcd.setCursor(0, 2);
  //-lcd.print(totalInches);
  //-lcd.print(" total inches");
  //-lcd.setCursor(0, 3);
  //-lcd.print(feet);
  //-lcd.print(" feet ");
  //-lcd.print(inches);
  //-lcd.print(" inches");
}
