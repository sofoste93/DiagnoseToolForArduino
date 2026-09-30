/*
  Arduino Mission Control demo protocol
  Streams A0 and board uptime; accepts START, PAUSE, STOP, STATUS and PINS.
*/
bool streaming = false;
unsigned long previousSample = 0;

void setup() {
  Serial.begin(9600);
  while (!Serial) { }
  Serial.println("READY");
}

void loop() {
  if (Serial.available()) {
    String command = Serial.readStringUntil('\n');
    command.trim();
    command.toUpperCase();
    if (command == "START") {
      streaming = true;
      Serial.println("STATE:1");
    } else if (command == "PAUSE" || command == "STOP") {
      streaming = false;
      Serial.println("STATE:0");
    } else if (command == "STATUS") {
      Serial.println(streaming ? "STATE:1" : "STATE:0");
    } else if (command.startsWith("PINS:")) {
      Serial.println("PINS_ACK:1");
    }
  }

  if (streaming && millis() - previousSample >= 250) {
    previousSample = millis();
    Serial.print("A0:");
    Serial.println(analogRead(A0));
    Serial.print("UPTIME:");
    Serial.println(millis() / 1000.0, 1);
  }
}
