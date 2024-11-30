package com.r7b7.service;

import com.r7b7.entity.Provider;

public class LLMServiceFactory {
    public static LLMService createService(Provider provider, String apiKey, String model) {
        return switch (provider) {
            case Provider.OPENAI -> new OpenAIService(apiKey, model);
            case Provider.ANTHROPIC -> new AnthropicService(apiKey, model);
            case Provider.OLLAMA -> new OllamaService(model);
            case Provider.GROQ -> new GroqService(apiKey, model);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    public static LLMService createService(Provider provider, String model) {
        return createService(provider, null, model);
    }
}
