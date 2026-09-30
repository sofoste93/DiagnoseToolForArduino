package com.sofoste.arduino;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Pattern;

public final class TelemetryParser {
    private static final Pattern SAMPLE = Pattern.compile(
            "^\\s*([A-Za-z][A-Za-z0-9_.-]*)\\s*[:=,]\\s*(-?(?:\\d+(?:\\.\\d+)?|\\.\\d+))\\s*$");

    public Optional<TelemetrySample> parse(String line) {
        if (line == null) return Optional.empty();
        var match = SAMPLE.matcher(line);
        if (!match.matches()) return Optional.empty();
        try {
            return Optional.of(new TelemetrySample(Instant.now(), match.group(1),
                    Double.parseDouble(match.group(2)), line.trim()));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
