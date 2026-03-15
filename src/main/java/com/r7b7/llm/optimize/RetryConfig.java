package com.r7b7.llm.optimize;

import java.time.Duration;

public record RetryConfig(int maxAttempts, Duration baseDelay, Duration maxDelay) {
    public RetryConfig {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        if (baseDelay == null || baseDelay.isNegative() || baseDelay.isZero()) {
            throw new IllegalArgumentException("baseDelay must be positive");
        }
        if (maxDelay == null || maxDelay.isNegative() || maxDelay.isZero()) {
            throw new IllegalArgumentException("maxDelay must be positive");
        }
    }

    public static RetryConfig defaults() {
        return new RetryConfig(3, Duration.ofMillis(250), Duration.ofSeconds(5));
    }
}
