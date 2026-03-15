package com.r7b7.llm.optimize;

public record AdaptiveTuningConfig(
        int defaultMaxTokens,
        int minMaxTokens,
        int maxMaxTokens,
        double defaultTemperature,
        double minTemperature,
        double maxTemperature) {

    public AdaptiveTuningConfig {
        if (defaultMaxTokens < 1) {
            throw new IllegalArgumentException("defaultMaxTokens must be >= 1");
        }
        if (minMaxTokens < 1 || maxMaxTokens < minMaxTokens) {
            throw new IllegalArgumentException("Invalid max token bounds");
        }
        if (defaultTemperature < 0.0) {
            throw new IllegalArgumentException("defaultTemperature must be >= 0.0");
        }
        if (minTemperature < 0.0 || maxTemperature < minTemperature) {
            throw new IllegalArgumentException("Invalid temperature bounds");
        }
    }

    public static AdaptiveTuningConfig defaults() {
        return new AdaptiveTuningConfig(
                1024,
                1,
                4096,
                0.2,
                0.0,
                2.0);
    }
}
