#include <Arduino.h>
#include <WiFi.h>
#include <FirebaseESP32.h>

#include <addons/TokenHelper.h>

#include <addons/RTDBHelper.h>

#define WIFI_SSID "Redmi Note 11"
#define WIFI_PASSWORD "1234abcd"
#define API_KEY "AIzaSyAyprH3lhPi3udbmH0RtfJnMG1ykm3XQjU"
#define DATABASE_URL "aether-database-default-rtdb.firebaseio.com"
#define USER_EMAIL "diegojnavarrol@gmail.com"
#define USER_PASSWORD "12345abcd"

FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;


void setup()
{

  Serial.begin(115200);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);
  config.api_key = API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  config.database_url = DATABASE_URL;

  config.token_status_callback = tokenStatusCallback;

  Firebase.reconnectNetwork(true);

  fbdo.setBSSLBufferSize(4096, 1024);

  Firebase.begin(&config, &auth);

  Firebase.setDoubleDigits(5);

}


void loop()
{

  if (Firebase.ready() && (millis() - sendDataPrevMillis > 5000 || sendDataPrevMillis == 0)){
    sendDataPrevMillis = millis();

    scheduleActiveLight = Firebase.getInt(fbdo, F("/jp101466/Dispositivos/Luces/Horario/Active")) ? fbdo.to<int>() : 0;
    Serial.println("Schedule: " + String(scheduleActiveLight));

    sensorActiveLight = Firebase.getInt(fbdo, F("/jp101466/Dispositivos/Luces/Sensor/Active")) ? fbdo.to<int>() : 0;
    Serial.println("Sensor: " + String(sensorActiveLight));

    customSensorLight = Firebase.getInt(fbdo, F("/jp101466/Dispositivos/Luces/Sensor/Intensidad/Intensity")) ? fbdo.to<int>() : 0;
    Serial.println("Custom: " + String(customSensorLight));

    customIntensity = Firebase.getInt(fbdo, F("/jp101466/Dispositivos/Luces/Sensor/Intensidad/Value")) ? fbdo.to<int>() : 0;
    Serial.println("Custom Value: " + String(customIntensity));

    startLightSchedule = Firebase.getDouble(fbdo, F("/jp101466/Dispositivos/Luces/Horario/Inicio")) ? fbdo.to<double>() : 0;
    Serial.println("Start: ");
    Serial.print(startLightSchedule, 4);
    Serial.println("");

    endLightSchedule = Firebase.getDouble(fbdo, F("/jp101466/Dispositivos/Luces/Horario/Fin")) ? fbdo.to<double>() : 0;
    Serial.println("End: ");
    Serial.print(endLightSchedule, 4);
    Serial.println("");
  }

}
