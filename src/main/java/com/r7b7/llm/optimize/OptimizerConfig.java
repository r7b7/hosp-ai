package com.r7b7.llm.optimize;

public record OptimizerConfig(RetryConfig retry, AdaptiveTuningConfig tuning) {
    public static OptimizerConfig defaults() {
        return new OptimizerConfig(RetryConfig.defaults(), AdaptiveTuningConfig.defaults());
    }
}
