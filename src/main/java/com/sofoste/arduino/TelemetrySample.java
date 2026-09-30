package com.sofoste.arduino;

import java.time.Instant;

public record TelemetrySample(Instant timestamp, String channel, double value, String raw) { }
