package com.r7b7.service;

import com.r7b7.client.LlmHttpClient;
import com.r7b7.entity.Provider;

public class LLMServiceFactory {

    public static ILLMService createService(Provider provider, String apiKey, String model, LlmHttpClient client) {
        return switch (provider) {
            case OPENAI -> new OpenAIService(apiKey, model, client);
            case ANTHROPIC -> new AnthropicService(apiKey, model, client);
            case OLLAMA -> new OllamaService(model, client);
            case GROQ -> new OpenAIService(apiKey, model, client);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }
}
