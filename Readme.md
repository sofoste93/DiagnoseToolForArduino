# ENGLISH
# Arduino Diagnostic Tool

>This repository contains a Java program designed to dialogue with an Arduino Uno through the serial port. It can send commands to the Arduino and log incoming sensor data.
>

## Software Requirement

1. Java (JDK 18 or later)
2. Arduino IDE
3. An Arduino Uno (or compatible) board

## Libraries Used

1. jSerialComm: This is a Java library for communicating via Serial Communication (i.e., COM ports)

## User Commands

Available commands for the Java program:

- `START` : Begin reading data from the Arduino Uno
- `PAUSE` : Pause data reading
- `PINS:<pin1,pin2,...>` : Specify which pins to read from
- `STOP` : Stop reading and close serial port
- `QUIT` : Terminate the program

## How to Use

1. Open the Arduino IDE, and upload the provided Arduino sketch to your Arduino Uno board. Make sure that serial data is being sent at a baud rate of 9600.
2. Note the port that the Arduino Uno is connected to (For windows it's usually "COMX", where X is the port number).
3. Run the provided Java code in your IDE of choice. When prompted in the console, enter one of the above commands.

>(!) Please note the Java program will log the sensor data into a file named "diagnoseLog.txt".
>
# DEUTSCH
# Diagnose Tool Für Arduino

Dieses Repository enthält ein Java-Programm, das dazu dient, über die serielle Schnittstelle mit einem Arduino Uno zu kommunizieren. Es kann Befehle an das Arduino senden und eingehende Sensor-Daten protokollieren.

## Software Anforderungen

1. Java (JDK 18 oder höher)
2. Arduino IDE
3. Ein Arduino Uno (oder kompatibles) Board

## Verwendete Bibliotheken

1. jSerialComm: Dies ist eine Java-Bibliothek, die zur Kommunikation über die serielle Schnittstelle (d.h. COM-Ports) dient.

## Nutzerbefehle

Verfügbare Befehle für das Java-Programm:

- `START` : Beginne Daten vom Arduino Uno zu lesen
- `PAUSE` : Pausiere das Lesen von Daten
- `PINS:<pin1,pin2,...>` : Gibt an, von welchen Pins gelesen werden soll
- `STOP` : Stoppe das Lesen und schließe den Seriell-Port
- `QUIT` : Beende das Programm

## Benutzungsanleitung

1. Öffne die Arduino IDE und lade den bereitgestellten Arduino-Sketch auf dein Arduino Uno Board. Stelle sicher, dass die seriellen Daten mit einer Baud-Rate von 9600 gesendet werden.
2. Notiere dir den Port, an dem das Arduino Uno angeschlossen ist (Für Windows ist das in der Regel "COMX", wobei X die Portnummer ist).
3. Führe den bereitgestellten Java-Code in deiner bevorzugten IDE aus. Wenn du in der Konsole dazu aufgefordert wirst, gib einen der obigen Befehle ein.

>(!)Bitte beachte, dass das Java-Programm die Sensordaten in eine Datei namens "diagnoseLog.txt" protokolliert.
>

> >### Contributions
>
> @sofoste93/ Stephane Sob Fouodji <br>
> A. Franz <br>
> Arduino https://www.arduino.cc/en/
# Diagnose-Tool für den Arduino

## TEIL 1
Dieses Programm verwendet die jSerialComm-Bibliothek, um eine serielle Verbindung zum Arduino über den Port "COM5" herzustellen.

Sobald eine Verbindung hergestellt ist, liest das Programm Daten von der seriellen Schnittstelle und schreibt diese Daten in eine Datei namens "daten.txt" oder "diagnoseLog.txt".

### Schritte zur Ausführung:

- 1. Fügen Sie die jSerialComm-Bibliothek zu Ihrem Projekt hinzu.
- 2. Führen Sie den DiagnoseTool-Code aus.
- 3. Überprüfen Sie die "daten.txt" ode "diagnoseLog.txt"-Datei auf die von Arduino gesendeten Daten.

### Wichtige Anmerkung:
- Stellen Sie sicher, dass der Baudrate-Wert (in diesem Fall 9600) dem Baudrate-Wert entspricht, der in Ihrem Arduino-Code festgelegt ist.
- Das Programm verbindet sich direkt mit "COM5". Falls Ihr Arduino an einem anderen Port angeschlossen ist, ändern Sie den Port entsprechend im Code.


## TEIL 2

### DiagnoseTool Erweiterung für Arduino in Java (Verwendung von COM5)

> Diese erweiterte Version ermöglicht es, den Auswertevorgang des Arduino über die serielle Schnittstelle zu steuern. Sie können den Vorgang starten, anhalten und die zu überwachenden Pins konfigurieren.

### Steuerbefehle:
>
> START: Beginnt den Auswertevorgang und schreibt die Daten in "daten.txt".
> PAUSE: Hält den Auswertevorgang an.
> PINS:<pin1,pin2,...>: Konfiguriert die zu überwachenden Pins.
> STOP: Stoppt den Auswertevorgang.
> QUIT: Beendet das Programm.
> Wichtige Hinweise:
>
Die Kommunikation erfolgt über Baudrate 9600. 
Stellen Sie sicher, dass der Arduino-Code dieselbe Baudrate verwendet.
Das Programm verbindet sich direkt mit "COM5". Bei Bedarf können Sie den Port im Code ändern.
Die Befehle sollten in der angegebenen Reihenfolge und Format eingegeben werden, damit sie korrekt interpretiert werden können.
Mit diesen Änderungen können Sie den Arduino effektiv über die serielle Schnittstelle von Ihrem PC aus steuern.
>
> ### Contributions
> 
> @sofoste93/ Stephane Sob Fouodji
> A. Franz 
> Arduino