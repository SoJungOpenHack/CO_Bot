#include <DHT11.h>
#include <SoftwareSerial.h>

//SoftwareSerial BTSerial(10, 11); // BLE(TXD) - RX(Arduino - 10) | BLE(RXD) - TX(Arduino - 11)
                                   
                                   // Serial1 (Arduino - Mega)
                                   // BLE(TXD) - RX(Arduino - 19) | BLE(RXD) - TX(Arduino - 18)
uint8_t buffer[256];
uint8_t index = 0;

// 3.3 v
//int Magnetic_Value = 0;
//int Magnetic_PIN = 2;   // Digital
//int Flame_Value = 0;
//int Flame_PIN = 5;      // Digital


// no Voltage
int LEDGREEN_PIN = 9;        // Digital
int LEDRED_PIN = 10;        // Digital

// 5.0v
int CO_Value = 100;
int CO_PIN = 3;         // Analog
int Buzzer_PIN = 12;    // Digital

int DHT_PIN=4;          // Digital
DHT11 dht11(DHT_PIN); 


void setup(){
//  pinMode(Magnetic_PIN, INPUT);
  //pinMode(Flame_PIN, INPUT); 
  pinMode(Buzzer_PIN,OUTPUT);
  pinMode(LEDGREEN_PIN, OUTPUT);
  pinMode(LEDRED_PIN, OUTPUT);
  
  //pinMode(13, OUTPUT);
//  Buzzer_OFF();
  
  Serial.begin(9600);        //Arduino Bluetooth - PC Serial
  Serial1.begin(9600);
  // PC   ==  Serial1.begin(115200);    //Arduino Bluetooth - Bluetooth connect Device
  // using Putty set baudrate
  Serial.println("Ready..");
  
}

void loop(){

  
  // 불꽃 발생시  0, 평상 시 1 반환
  //Flame_Check();
  
  // 문 닫혀있을 경우(자석 붙어있음) 0, 열려있을 경우(자석 떨어짐) 1 반환
  //Magnetic_Check();
  
  // 라이터 가스 주입 시 500 이상 유지 평사시 300 ~ 500 미만 사이 유지
  CO_Check();
  Serial.println("temp");
 Temp_Check();  
  Serial.print("index=");
  Serial.println(index);
  
  Serial1.write(buffer,index);
   for(uint8_t i = 0; i< index; i++){
     Serial.println(buffer[i]);
     delay(50);
   }

   
   delay(50);

  index = 0;
}


// 불꽃 발생시  0, 평상 시 1 반환
/*void Flame_Check(){
  Flame_Value = digitalRead(Flame_PIN);
  
  if(Flame_Value == HIGH){
    Buzzer_OFF();
    ColorLed_OFF();
    buffer[index++] = 's';
  }else{
    ColorLed_ON();
    //Buzzer_ON();
    buffer[index++] = 'f';
  }
  Serial.print("FlameValue : \r ");
  Serial.print(Flame_Value, DEC);
  Serial.println("\n");
  delay(50);
  
  Flame_Value = 0;
}

// 문 닫혀있을 경우(자석 붙어있음) 0, 열려있을 경우(자석 떨어짐) 1 반환
void Magnetic_Check(){
  Magnetic_Value = digitalRead(Magnetic_PIN);
  
  if(Magnetic_Value == HIGH){
    ColorLed_ON();
   buffer[index++] = 'o';
  }else{
    ColorLed_OFF();
   buffer[index++] = 'c';
  }
  Serial.print("MagneticValue : \r ");
  Serial.print(Magnetic_Value, DEC);
  Serial.println("\n");
  delay(50);
  
  Magnetic_Value = 0;
}
*/
// 라이터 가스 주입 시 500 이상 유지 평사시 300 ~ 500 미만 사이 유지
void CO_Check(){
  CO_Value = analogRead(CO_PIN) * 3;
/*
  // 1단계_안전(~400)
  if(CO_Value < 400) {
    ColorLed_ON();
    buffer[index++] = 'a';
  }
  // 2단계 (400~600)
  else if (CO_Value >= 400 && CO_Value < 600) {
    buffer[index++] = 'b';
  }
  // 3단계 (600~1000)
  else if (CO_Value >= 600 && CO_Value < 1000) {
    buffer[index++] = 'c';
  } 
  else {
    ColorLed_OFF();
    buffer[index++] = 'd';
  }*/

  

    
  Serial.print("CO_Value : \r ");
  Serial.print(CO_Value, DEC);
  Serial.println("\n");
  Serial.println(index);

  pack32(CO_Value,buffer);

  delay(50);
  
  CO_Value = 0;
  
  
}

void pack32(uint32_t val,uint8_t *dest)
{
        dest[index++] = (val & 0xff000000) >> 24;
        dest[index++] = (val & 0x00ff0000) >> 16;
        dest[index++] = (val & 0x0000ff00) >>  8;
        dest[index++] = (val & 0x000000ff)      ;
}



void Temp_Check(){
  int err;
  float temp,humi;
 
  
  if((err=dht11.read(temp,humi))==0)
  {
    buffer[index++] = temp;
   // buffer[index++] = humi;
  }

    Serial.print(temp);
   
  delay(DHT11_RETRY_DELAY); //delay for reread
}
/*
void Buzzer_ON(){
  int i;
    for(i=0;i<80;i++){ //output sound of one frequency
      digitalWrite(Buzzer_PIN,HIGH);//make a sound
      delay(1);//delay 1ms
      digitalWrite(Buzzer_PIN,LOW);//silient
      delay(1);//delay 1ms
    }
    for(i=0;i<300;i++) {//output sound of another frequency 
      digitalWrite(Buzzer_PIN,HIGH);//make a sound
      delay(2);//delay 2ms
      digitalWrite(Buzzer_PIN,LOW);//silient 
      delay(2);//delay 2ms
    }
}
void Buzzer_OFF(){
   digitalWrite(Buzzer_PIN,LOW);
}

// RED 점멸
void ColorLed_ON(){
  digitalWrite(LEDGREEN_PIN,HIGH);
}

// GREEN 점멸
void ColorLed_OFF(){
  digitalWrite(LEDGREEN_PIN, LOW);
}*/
