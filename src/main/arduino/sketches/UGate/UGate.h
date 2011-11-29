//======= Node ID that identifies the node accross the network =======
#define NODE_ID 1
//======= General =======
#define NUMOFELEM(a) (sizeof(a)/sizeof(a[0]))
//======= Exception Handlers =======
// include "Exceptions.h"
//======= XBee (XBee Adapter with XBee Pro 60mW Chip Antenna [~1 Mile Range]) =======
// unless you have MY on the receiving radio set to FFFF, receives will be RX16 packets
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
#define CMD_SERVO_TILT_UP 16
#define CMD_SERVO_TILT_DWN 17
#define CMD_SERVO_PAN_RIGHT 18
#define CMD_SERVO_PAN_LEFT 19
#define CMD_SESSION_RESET 20
#define CMD_TOGGLE_ALARMS 21
#define CMD_TAKE_SEND_PIC 29
#define CMD_CHG_KEYS 37
#define CMD_TOOGLE_SERVO 58
#define CMD_TOOGLE_GATE 59
#define CMD_SERVO_CAM 100 // flag to indicate cam servo selection
#define CMD_SERVO_SONAR_IR 101 // flag to indicate sonar/IR servo selection
#define CMD_SERVO_MICROWAVE 102 // flag to indicate microwave servo selection
#define CMD_SENSOR_READINGS_SEND 103
#define CMD_SENSOR_SETTINGS_SEND 104
#define CMD_SENSOR_SETTINGS_SET 105
#define ALRM_SONAR 0
#define ALRM_IR 1
#define ALRM_MW 2
#define ALRM_TRIP_ANY 0 // Any sensor that is tripped will signal alarm
#define ALRM_TRIP_SONAR_IR 1 // Sonar and IR have to be tripped in order to signal alarm
#define ALRM_TRIP_SONAR_MW 2 // Sonar and Microwave have to be tripped in order to signal alarm
#define ALRM_TRIP_IR_MW 3 // IR and Microwave have to be tripped in order to signal alarm
#define ALRM_TRIP_ALL 4 // All sensors have to be tripped in order to signal alarm
#define ALRMISTRIPPED(alrm) \
  (((alrm) == ALRM_SONAR && alarmState == ALRM_TRIP_ANY && sonarTripOn && sonarReadInches >= sonarLimitInches) || \
   ((alrm) == ALRM_IR && alarmState == ALRM_TRIP_ANY && irTripOn && irReadInches >= sonarLimitInches) || \
   ((alrm) == ALRM_MW && alarmState == ALRM_TRIP_ANY && mwTripOn && mwCycleCnt >= mwLimitCycles) || \
   (((alrm) == ALRM_SONAR || (alrm) == ALRM_IR) && alarmState == ALRM_TRIP_SONAR_IR && sonarTripOn && irTripOn && sonarReadInches >= sonarLimitInches && irReadInches >= irLimitInches) || \
   (((alrm) == ALRM_SONAR || (alrm) == ALRM_MW) && alarmState == ALRM_TRIP_SONAR_MW && sonarTripOn && mwTripOn && sonarReadInches >= sonarLimitInches && mwCycleCnt >= mwLimitCycles) || \
   (((alrm) == ALRM_IR || (alrm) == ALRM_MW) && alarmState == ALRM_TRIP_IR_MW && irTripOn && mwTripOn && irReadInches >= irLimitInches && mwCycleCnt >= mwLimitCycles) || \
   (alarmState == ALRM_TRIP_ALL && sonarTripOn && irTripOn && mwTripOn && sonarReadInches >= sonarLimitInches && irReadInches >= irLimitInches && mwCycleCnt >= mwLimitCycles))
int cmdNA = -1; // invalid command indicator
volatile int cmdBuffer[] = {cmdNA,cmdNA,cmdNA}; // commands waiting to be executed
byte key0 = 1, key1 = 2, key2 = 3; // default access key codes
int tripCmd = CMD_TAKE_SEND_PIC; // default trip command is to take a pic and send to host
int alarmState = ALRM_TRIP_ANY;
//======= Camera (Serial CCD Electronic Brick Camera [Seed Studio]) =======
#define CAM_IR_BOARD_PIN 30
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
#define SONAR_TX_PIN 3
#define SONAR_RX_PIN 4
unsigned long sonarLimitInches = 60; // default inches that will cause a sonar alarm trip
unsigned long sonarReadInches; // current or last sonar distance reading
int sonarTripDelay; // sonar delay between alarm trips (in minutes)
int sonarTripOn = 1; // sonar alarm trip on/off
//======= IR Remote Control Receiver (TSOP38238) =======
// uno ::: IR must be on Pin 2 (INT0), Pin 3 (INT1) for hardware interrupt
// uno32 ::: IR must be on Pin 38 (INT0), Pin 2 (INT1), Pin 7 (INT2), Pin 8 (INT3), Pin 35 (INT4) for hardware interrupt
#define IR_LED_PIN 8
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
unsigned long irReadInches; // current or last IR distance reading
int irTripDelay; // IR delay between alarm trips (in minutes)
int irTripOn = 1; // IR alarm trip on/off
//======= Microwave (X-Band 10.525 GHz Microwave Motion/Speed Sensor [~30ft Range]) =======
#define MW_EN_PIN 6
#define MW_PIN 7 // if microwave read pin number changes so should the read in MWPINREAD
#define MWPINREAD() (PORTDbits.RD9) // MWPINREAD() (digitalRead(MW_PIN)) will render course resolution LATDbits.LATD9
#define MW_MS 250 // milliseconds for sample microwave cycle count
unsigned int mwLimitCycles = 15; // default num of cycles/sec for alarm trip
volatile byte mwTrigger; // flag to indicate if the microwave read should be performed - INTERRUPT ONLY!
int mwCycleCnt, mwTripDelay; // current or last microwave cycle count reading, microwave delay between alarm trips (in minutes)
int mwTripOn = 1; // microwave alarm trip on/off
//======= Gate =======
int gateOpen; // state of the gate
//======= Servos =======
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
#define SERVOISCMD(cmd) ((cmd) == CMD_SERVO_TILT_UP || (cmd) == CMD_SERVO_TILT_DWN || (cmd) == CMD_SERVO_PAN_RIGHT || (cmd) == CMD_SERVO_PAN_LEFT) // is cmd for a servo movement
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
  if ((cmd) == CMD_SERVO_TILT_UP || (cmd) == CMD_SERVO_TILT_DWN) { \
    servoTiltSonar.attach(SERVO_SONAR_TILT_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoTiltSonar.write(servoTiltSonar.read() + ((cmd) == CMD_SERVO_TILT_UP ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } else if ((cmd) == CMD_SERVO_PAN_RIGHT || (cmd) == CMD_SERVO_PAN_LEFT) { \
    servoPanSonar.attach(SERVO_SONAR_PAN_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoPanSonar.write(servoPanSonar.read() + ((cmd) == CMD_SERVO_PAN_RIGHT ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } \
} else if (servoSelect == CMD_SERVO_MICROWAVE) { \
  if ((cmd) == CMD_SERVO_PAN_RIGHT || (cmd) == CMD_SERVO_PAN_LEFT) { \
    servoPanMw.attach(SERVO_MICROWAVE_PAN_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoPanMw.write(servoPanMw.read() + ((cmd) == CMD_SERVO_PAN_RIGHT ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } \
} else { \
  if ((cmd) == CMD_SERVO_TILT_UP || (cmd) == CMD_SERVO_TILT_DWN) { \
    servoTiltCam.attach(SERVO_CAM_TILT_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoTiltCam.write(servoTiltCam.read() + ((cmd) == CMD_SERVO_TILT_UP ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } else if ((cmd) == CMD_SERVO_PAN_RIGHT || (cmd) == CMD_SERVO_PAN_LEFT) { \
    servoPanCam.attach(SERVO_CAM_PAN_PIN, SERVO_MIN_ANGLE, SERVO_MAX_ANGLE); \
    servoPanCam.write(servoPanCam.read() + ((cmd) == CMD_SERVO_PAN_RIGHT ? SERVO_INC_ANGLE : SERVO_INC_ANGLE * -1)); \
  } \
}
