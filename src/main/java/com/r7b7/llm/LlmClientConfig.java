package com.r7b7.llm;

import java.net.URI;
import java.time.Duration;

import com.r7b7.entity.Provider;

public record LlmClientConfig(
        Provider provider,
        String apiKey,
        String model,
        URI baseUri,
        String anthropicVersion,
        Duration requestTimeout) {
}
