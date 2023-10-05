package sob.sofoste;

import com.fazecast.jSerialComm.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // Directly get COM5 port
        SerialPort arduinoPort = SerialPort.getCommPort("COM5");

        // Check if the port is open
        if (!arduinoPort.openPort()) {
            System.out.println("Fehler beim Öffnen des Ports: COM5");
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

