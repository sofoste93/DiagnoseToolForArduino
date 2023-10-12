package sob.sofoste;

import com.fazecast.jSerialComm.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * 2. Diagnose-Tool für den Arduino
 * Version 1
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
public class DiagnoseToolVersionOne {

    public static void main(String[] args) {
        // Directly get COM5 port
        SerialPort arduinoPort = SerialPort.getCommPort("COM6");

        // Check if the port is open
        if (!arduinoPort.openPort()) {
            System.out.println("Error while opening the port: COM6");
            return;
        }

        arduinoPort.setBaudRate(9600);

        // Read data and write it to a file
        try (FileWriter writer = new FileWriter(new File("daten.txt"))) {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 30000) { // Let it run for 30 seconds
                while (arduinoPort.bytesAvailable() == 0) {
                    Thread.sleep(20);
                }

                byte[] readBuffer = new byte[arduinoPort.bytesAvailable()];
                int numRead = arduinoPort.readBytes(readBuffer, readBuffer.length);
                if(numRead < 0) {
                    System.out.println("Error reading from port.");
                    break;
                }
                String result = new String(readBuffer);
                writer.write(result);
                writer.flush();

                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            arduinoPort.closePort();
        }
    }
}

