package com.r7b7.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.r7b7.client.OllamaClient;
import com.r7b7.client.factory.OllamaClientFactory;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Param;
import com.r7b7.model.BaseLLMResponse;
import com.r7b7.model.LLMRequest;
import com.r7b7.model.LLMResponse;

public class OllamaService implements LLMService {
    private final String model;

    public OllamaService(String model) {
        this.model = model;
    }

    @Override
    public LLMResponse generateResponse(LLMRequest request) {
        OllamaClient client = OllamaClientFactory.getClient();
        Map<String, Object> platformAllignedParams = getPlatformAllignedParams(request);

        CompletionResponse response = client
                .generateCompletion(new CompletionRequest(request.getPrompt(), platformAllignedParams, model, null));
        Map<String, Object> metadata = Map.of(
                "model", model,
                "provider", "Ollama");

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
                    Param.seed, "seed",
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
