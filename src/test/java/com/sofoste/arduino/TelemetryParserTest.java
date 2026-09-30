package com.sofoste.arduino;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryParserTest {
    private final TelemetryParser parser = new TelemetryParser();

    @Test void parsesSupportedSeparatorsAndNumbers() {
        assertEquals(512, parser.parse("A0:512").orElseThrow().value());
        assertEquals(23.4, parser.parse("TEMP = 23.4").orElseThrow().value(), .001);
        assertEquals(-4.5, parser.parse("offset,-4.5").orElseThrow().value(), .001);
    }

    @Test void rejectsMessagesThatAreNotTelemetry() {
        assertTrue(parser.parse("READY").isEmpty());
        assertTrue(parser.parse("A0:not-a-number").isEmpty());
        assertTrue(parser.parse("").isEmpty());
    }
}
