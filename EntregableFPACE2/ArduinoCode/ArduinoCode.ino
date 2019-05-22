#include <Arduino.h>
long START_HR;
long FINAL_HR;
//STATUS
int DUCH_STATUS;

// DIGITAL PIN FOR BUTTONS
#define btnStart 22
#define btnManual  23
#define btnAutomatic  25
#define btnSave  27

// VARIABLE FOR READING THE PUSHBUTTON STATUS
int btnManualState;
int btnAutomaticState;
int btnSaveState;
int btnStartState;
int RUNNING;
//VARS FOR MOVEMENT SENSOR
#define TRIGGER_PIN A8
#define ECHO_PIN A9
long DISTANCE_CM;

//FOR TURNING OFF AND TURNING ON THE SHOWER
#define SHOWER_PIN 10

//VALUE GOTTEN FROM ANDROID
char CHAR_GOTTEN = '\0';

//FOR MODULE SMD RGB
#define LED_R 4
#define LED_G 5
#define LED_B 6

// VARS TO READ FLOW SENSOR
const int sensorPin = 2; // 0 = digital pin 2
const int measureInterval = 500;
volatile int pulseConter;
// YF-S201
const float KFACTOR = 7.5; //const float KFACTOR = 7.5;
float WATER_VOLUMEN = 0;
float WATER_VOLUMEN_MAX = 10;
long t_0 = 0;

void setup() {
        Serial.begin(9600);
        pinMode(TRIGGER_PIN, OUTPUT);
        pinMode(ECHO_PIN, INPUT);
        DISTANCE_CM = 0;

        pinMode(SHOWER_PIN, OUTPUT);

        DUCH_STATUS = -1;
        START_HR = 0; /** START TIME  **/
        FINAL_HR = 30; /** END TIME  **/


        //SET PINOUT SMD RGB MODULE, INITIALIZE THE PINS AS AN  OUTPUT.
        pinMode(LED_R, OUTPUT);
        pinMode(LED_G, OUTPUT);
        pinMode(LED_B, OUTPUT);

        // SENSOR YF-S201
        attachInterrupt(digitalPinToInterrupt(sensorPin), ISRCountPulse, RISING);
        t_0 = millis();

        // START AND SELECT BUTTONS, INITIALIZE THE PUSHBUTTONS PINS AS AN INPUT:
        pinMode(btnStart, INPUT);
        pinMode(btnManual, INPUT);
        pinMode(btnAutomatic, INPUT);
        pinMode(btnSave, INPUT);
        btnManualState = 0;
        btnAutomaticState = 0;
        btnSaveState = 0;
        btnStartState = 0;
        RUNNING = 0;

}
void loop() { //METHOD YOU KNOW
        //**********************************************************************
        //                              BLUETOOTH
        //**********************************************************************
        if (Serial.available()) {
                getDataAndroid();
        }
        //**********************************************************************
        btnStartState = digitalRead(btnStart);
        if (btnStartState == HIGH) {
                //        Serial.println("btnStart was pushed.");
                START_HR = millis(); /** STARTING TIME  **/
                RUNNING = 1;
        }
        btnManualState = digitalRead(btnManual);
        if (btnManualState == HIGH) {
                DUCH_STATUS = 1;
                signalLED('R');
        }
        btnAutomaticState = digitalRead(btnAutomatic);
        if ( btnAutomaticState == HIGH) {
                DUCH_STATUS = 2;
                signalLED('B');
        }
        btnSaveState = digitalRead(btnSave);
        if ( btnSaveState == HIGH) {
                DUCH_STATUS = 3;
                signalLED('G');
        }
        //calculateFlow();
        if ( (RUNNING == 0) ) {
          return;
        }
        else if ( (DUCH_STATUS == 1) && (RUNNING == 1 )) { // MANUAL MODE
                calculateFlow();
                if ( (((millis() - START_HR) / 1000) < FINAL_HR )  ) {
                        if (((FINAL_HR - (( millis() - START_HR ) / 1000 )  ) )  < 10 ) {
                                signalLED('A');
                        }
                        if ( WATER_VOLUMEN < WATER_VOLUMEN_MAX ) {
                                if ( isThereMovement() ) {
                                        turnOnShower( 2 );
                                }
                                else{
                                        turnOffShower();
                                }
                        }
                }
                else {
                        turnOffShower();
                        signalLED('N');
                        // the next code i uses to send inf to ANDROID
                        Serial.print( "@" );
                        Serial.print( WATER_VOLUMEN, 3 );
                        RUNNING = 0;
                        START_HR = 0;
                        FINAL_HR = 30;
                        DUCH_STATUS = 0;
                        WATER_VOLUMEN = 0;
                        WATER_VOLUMEN_MAX = 10;
                }
        }
        else if ( (DUCH_STATUS == 2) && (RUNNING == 1) ) { // AUTOMATIC MODE
                calculateFlow();
                if ( ((millis() - START_HR) / 1000) < FINAL_HR ) {
                        if (((FINAL_HR - (( millis() - START_HR ) / 1000 )  ) )  < 10 ) {
                                signalLED('A');
                        }
                        if ( isThereMovement() ) {
                                turnOnShower( 2 );
                        }
                        else{
                                turnOffShower();
                        }
                }
                else {
                        turnOffShower();
                        signalLED('N');
                        // the next code i uses to send inf to ANDROID
                        Serial.print( "@" );
                        Serial.print( WATER_VOLUMEN, 3 );
                        RUNNING = 0;
                        START_HR = 0;
                        FINAL_HR = 30;
                        DUCH_STATUS = 0;
                        WATER_VOLUMEN = 0;
                        WATER_VOLUMEN = 0;
                        WATER_VOLUMEN_MAX = 10;
                }

        }
        else if ( (DUCH_STATUS == 3) && (RUNNING == 1) ) { //SAVE MODE
                calculateFlow();
                // Serial.println("SAVE MODE: ");
                // Serial.println("hora ini: ");
                // Serial.println(START_HR);
                // Serial.println("hora actual: ");
                // Serial.println( ((millis() - START_HR) / 1000));
                // Serial.println("hora fin: ");
                // Serial.println(FINAL_HR);
                if ( ((millis() - START_HR) / 1000) < FINAL_HR ) {
                        if (((FINAL_HR - (( millis() - START_HR ) / 1000 )  ) )  < 10 ) {
                                signalLED('A');
                        }
                        if ( isThereMovement() ) {
                                turnOnShower( 2 );
                        }
                        else{
                                turnOffShower();
                        }
                }
                else {
                        turnOffShower();
                        signalLED('N');
                        // the next code i uses to send inf to ANDROID
                        Serial.print( "@" );
                        Serial.print( WATER_VOLUMEN, 3 );
                        RUNNING = 0;
                        START_HR = 0;
                        FINAL_HR = 30;
                        DUCH_STATUS = 0;
                        WATER_VOLUMEN = 0;
                        WATER_VOLUMEN = 0;
                        WATER_VOLUMEN_MAX = 10;
                }
        }
}
void signalLED(char opcion){ //METHOD DRIVER MODULE RGB
        switch ( opcion ) {
        case 'R':
                digitalWrite(LED_R, HIGH);
                digitalWrite(LED_G, LOW);
                digitalWrite(LED_B, LOW);
                break;
        case 'G':
                digitalWrite(LED_R, LOW);
                digitalWrite(LED_G, HIGH);
                digitalWrite(LED_B, LOW);
                break;
        case 'B':
                digitalWrite(LED_R, LOW);
                digitalWrite(LED_G, LOW);
                digitalWrite(LED_B, HIGH);
                break;
        case 'A':
                digitalWrite(LED_R, HIGH);
                digitalWrite(LED_G, HIGH);
                digitalWrite(LED_B, LOW);
                break;
        case 'N':
                digitalWrite(LED_R, LOW);
                digitalWrite(LED_G, LOW);
                digitalWrite(LED_B, LOW);
                break;
        default:
                digitalWrite(LED_R, LOW);
                digitalWrite(LED_G, LOW);
                digitalWrite(LED_B, LOW);
                break;
        }
}
void turnOnShower(int seconds){ //METHOD TURN ON THE SHOWER
        digitalWrite(SHOWER_PIN, HIGH);
        //delay( seconds * 1000 );
}
void turnOffShower(){ //METHOD TURN OFF THE SHOWER
        digitalWrite(SHOWER_PIN, LOW);
}
void readMovementSensor() { //METHOD THAT GETS VALUES FROM MOVEMENT SENSOR
        long duration;

        digitalWrite(TRIGGER_PIN, LOW);
        delayMicroseconds(4);
        digitalWrite(TRIGGER_PIN, HIGH);
        delayMicroseconds(10);
        digitalWrite(TRIGGER_PIN, LOW);

        duration = pulseIn(ECHO_PIN, HIGH);

        DISTANCE_CM = duration * 10 / 292/ 2;
}
bool isThereMovement(){ //METHOD THAT TESTS IF THERE WAS MOVEMENT, AND THEN I CAN TURN THE SHOWER ON
        readMovementSensor();
        //if ( (DISTANCE_CM >= 25) &  (DISTANCE_CM <= 50) ) {
        if ( (DISTANCE_CM >= 22) &  (DISTANCE_CM <= 28) ) {
                return true;
        }
        else {
                return false;
        }
}
void sendDataAndroid(){ //METHOD FOR SENDING DATA TO ANDROID, IT LEFTS.

}
void getDataAndroid() { //METHOD FOR GETTING DATA FROM ANDROID *****
        CHAR_GOTTEN = Serial.read();

        if (CHAR_GOTTEN == '0') { // TURN ELECTRIC VALVE OFF
                DUCH_STATUS = 0;
        }
        else if (CHAR_GOTTEN == '1') { // MANUAL MODE
                DUCH_STATUS = 1;
                signalLED('R');
        }
        else if (CHAR_GOTTEN == '2') { // AUTOMATIC MODE
          FINAL_HR = 600;
                DUCH_STATUS = 2;
                signalLED('B');
        }
        else if (CHAR_GOTTEN == '3') { //  SAVE MODE
          FINAL_HR = 190;
                DUCH_STATUS = 3;
                signalLED('G');
        }
        else if (CHAR_GOTTEN == '4') { //  TURN THE SHOWER ON
                turnOnShower(2);
        }
        else if (CHAR_GOTTEN == '5') { //  TURN THE SHOWER OFF
                turnOffShower();
        }
        else if (CHAR_GOTTEN == 'M') { //  MANUAL MODE
                DUCH_STATUS = 1;
                RUNNING = 1;
                START_HR = millis(); /** STARTING TIME  **/
                signalLED('R');
                Serial.print("MANUAL MODE.");
        }
        else if (CHAR_GOTTEN == 'A') { //  AUTOMATIC MODE
          FINAL_HR = 600;
                DUCH_STATUS = 2;
                RUNNING = 1;
                START_HR = millis(); /** STARTING TIME  **/
                signalLED('B');
                Serial.print("AUTOMATIC MODE.");
        }
        else if (CHAR_GOTTEN == 'S') { //  SAVE MODE
          FINAL_HR = 190;
                DUCH_STATUS = 3;
                RUNNING = 1;
                START_HR = millis(); /** STARTING TIME  **/
                signalLED('G');
                Serial.print("SAVE MODE: ");
        }
        else if (CHAR_GOTTEN == '#') { //
                String inString = "";
                inString = Serial.readString();
                FINAL_HR = String(inString).toInt();
                //Serial.print("Value tiempo: ");
                Serial.print( FINAL_HR );
        }
        else if (CHAR_GOTTEN == '|') { //** get distancia
                String inString = "";
                inString = Serial.readString();
                WATER_VOLUMEN_MAX =  String(inString).toInt();
                //Serial.print("Value volume: ");
                Serial.print( WATER_VOLUMEN_MAX );
        }
}
void ISRCountPulse(){//METHOD FOR COUNTING PULSE
        pulseConter++;
}
float GetFrequency(){//METHOD TO GET CAUDAL
        pulseConter = 0;

        interrupts();
        delay(measureInterval);
        noInterrupts();

        return (float)pulseConter * 1000 / measureInterval;
}
void SumWATER_VOLUMEN(float dV){//METHOD TO INTEGRATE WITH RESPECT TO TIME
        WATER_VOLUMEN += dV / 60 * (millis() - t_0) / 1000.0;
        t_0 = millis();
}
void calculateFlow(){ //METHOD TO CALCULATE FLOW
        // get frecuencia en Hz
        float frequency = GetFrequency();
        // calculate caudal L/min
        float flow_Lmin = frequency / KFACTOR;
        SumWATER_VOLUMEN(flow_Lmin);
        // Serial.print(" Caudal: ");
        // Serial.print(flow_Lmin, 3);
        // Serial.print(" (L/min)\tConsumo:");
        // Serial.print(WATER_VOLUMEN, 1);
        // Serial.println(" (L)");
}
