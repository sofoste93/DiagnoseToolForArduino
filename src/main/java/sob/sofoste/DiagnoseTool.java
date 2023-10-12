package sob.sofoste;

import com.fazecast.jSerialComm.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDateTime;  // For creating timestamps
/**
 * 2. Diagnose-Tool für den Arduino
 * Version 2 (Mit Erweiterung)
 * ===========================================================================================================
 * Erstellt werden soll ein Software-Tool, welches die Schnittstelle ausliest, über die serielle Schnittstelle
 * (USB-Port) an den PC (Host) übergibt.
 * Im PC sollen diese Werte in einer Protokolldatei archiviert werden.
 * ##############################################################################################
 * In einer Erweiterung soll der Auswertevorgang vom PC angestoßen, angehalten/unterbrochen und
 * beendet werden können.
 * Außerdem soll eine Konfigurierbarkeit bezüglich der auszuwertenden Schnittstellen (Pins) möglich
 * sein.
 */

public class DiagnoseTool {
    public static void main(String[] args) {
        SerialPort arduinoPort = SerialPort.getCommPort("COM6");

        if (!arduinoPort.openPort()) {
            System.out.println("Error while opening the port: COM6");
            return;
        }

        arduinoPort.setBaudRate(9600);

        Scanner userInput = new Scanner(System.in);

        while (true) {
            System.out.println("Befehle: START, PAUSE, PINS:<pin1,pin2,...>, STOP, QUIT"); // wenn von Arduino supported
            String command = userInput.nextLine();

            if (command.equalsIgnoreCase("QUIT")) {
                userInput.close();
                break;
            }

            arduinoPort.writeBytes(command.getBytes(), command.length());

            if (command.equalsIgnoreCase("START")) {
                try (FileWriter writer = new FileWriter(new File("diagnoseLog.txt"))) {
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 30000) {
                        while (arduinoPort.bytesAvailable() == 0) {
                            Thread.sleep(20);
                        }
                        byte[] readBuffer = new byte[arduinoPort.bytesAvailable()];
                        int numRead = arduinoPort.readBytes(readBuffer, readBuffer.length);
                        if(numRead < 0) {
                            System.out.println("Error while reading the port.");
                            break;
                        }
                        //Here create the timestamp and add it alongside the value
                        String result = LocalDateTime.now()+" - Port: COM6 - Value: "+new String(readBuffer);
                        //Write the data to the file
                        writer.write(result);
                        writer.flush();
                        Thread.sleep(1000);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        arduinoPort.closePort();
    }
}
