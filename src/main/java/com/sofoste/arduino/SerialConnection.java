package com.sofoste.arduino;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class SerialConnection implements AutoCloseable {
    public record Port(String name, String description) {
        @Override public String toString() { return name + "  ·  " + description; }
    }

    private SerialPort port;

    public List<Port> availablePorts() {
        return Arrays.stream(SerialPort.getCommPorts())
                .map(item -> new Port(item.getSystemPortName(), cleanDescription(item)))
                .toList();
    }

    public synchronized void connect(String portName, int baudRate, Consumer<byte[]> receiver) {
        close();
        var candidate = SerialPort.getCommPort(portName);
        candidate.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        candidate.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        if (!candidate.openPort()) throw new IllegalStateException("Could not open " + portName + ".");
        candidate.addDataListener(new SerialPortDataListener() {
            @Override public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
            @Override public void serialEvent(SerialPortEvent event) {
                int count = candidate.bytesAvailable();
                if (count <= 0) return;
                byte[] buffer = new byte[count];
                int read = candidate.readBytes(buffer, buffer.length);
                if (read > 0) receiver.accept(read == buffer.length ? buffer : Arrays.copyOf(buffer, read));
            }
        });
        port = candidate;
    }

    public synchronized void send(String command) {
        if (!isConnected()) throw new IllegalStateException("No board is connected.");
        byte[] bytes = command.getBytes(StandardCharsets.UTF_8);
        if (port.writeBytes(bytes, bytes.length) != bytes.length)
            throw new IllegalStateException("The complete command could not be sent.");
    }

    public synchronized boolean isConnected() { return port != null && port.isOpen(); }

    @Override public synchronized void close() {
        if (port != null) {
            port.removeDataListener();
            if (port.isOpen()) port.closePort();
            port = null;
        }
    }

    private static String cleanDescription(SerialPort port) {
        String value = port.getDescriptivePortName();
        return value == null || value.isBlank() ? "Serial device" : value;
    }
}
