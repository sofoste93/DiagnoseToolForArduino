package com.sofoste.arduino;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class SessionLog implements AutoCloseable {
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.systemDefault());
    private final Path file;
    private final BufferedWriter writer;

    public SessionLog(Path directory) {
        try {
            Files.createDirectories(directory);
            file = directory.resolve("telemetry-" + FILE_TIME.format(Instant.now()) + ".csv");
            writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
            writer.write("timestamp,channel,value,raw\n");
            writer.flush();
        } catch (IOException error) {
            throw new IllegalStateException("Could not create the session log.", error);
        }
    }

    public synchronized void append(TelemetrySample sample) {
        try {
            writer.write(sample.timestamp() + "," + csv(sample.channel()) + "," + sample.value() + "," + csv(sample.raw()) + "\n");
            writer.flush();
        } catch (IOException error) {
            throw new IllegalStateException("Could not write telemetry.", error);
        }
    }

    public Path file() { return file; }
    @Override public synchronized void close() { try { writer.close(); } catch (IOException ignored) { } }
    private static String csv(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
}
