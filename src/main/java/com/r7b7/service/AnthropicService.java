package com.r7b7.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.r7b7.client.AnthropicClient;
import com.r7b7.client.factory.AnthropicClientFactory;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Param;
import com.r7b7.model.BaseLLMResponse;
import com.r7b7.model.LLMRequest;
import com.r7b7.model.LLMResponse;

public class AnthropicService implements LLMService {
    private final String apiKey;
    private final String model;

    public AnthropicService(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public LLMResponse generateResponse(LLMRequest request) {
        AnthropicClient client = AnthropicClientFactory.getClient();
        Map<String, Object> platformAllignedParams = null;

        platformAllignedParams = getPlatformAllignedParams(request);
        CompletionResponse response = client.generateCompletion(
                new CompletionRequest(request.getPrompt(), platformAllignedParams, model, apiKey));

        Map<String, Object> metadata = Map.of(
                "model", model,
                "provider", "anthropic");
        return new BaseLLMResponse(response, metadata);
    }

    @Override
    public CompletableFuture<LLMResponse> generateResponseAsync(LLMRequest request) {
        return CompletableFuture.supplyAsync(() -> generateResponse(request));
    }

    private Map<String, Object> getPlatformAllignedParams(LLMRequest request) {
        Map<String, Object> platformAllignedParams = null;
        if (null != request.getParameters()) {
            Map<Param, String> keyMapping = Map.of(
                    Param.max_token, "max_tokens",
                    Param.temperature, "temperature");

            platformAllignedParams = request.getParameters().entrySet().stream()
                    .filter(entry -> keyMapping.containsKey(entry.getKey()))
                    .collect(Collectors.toMap(
                            entry -> keyMapping.get(entry.getKey()),
                            Map.Entry::getValue));
        }
        return platformAllignedParams;
    }
}
