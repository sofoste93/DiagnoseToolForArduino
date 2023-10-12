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
> Herr Franz /TLS<br>
> Arduino https://www.arduino.cc/en/