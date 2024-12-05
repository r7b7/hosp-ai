package com.r7b7.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.client.IOllamaClient;
import com.r7b7.client.factory.LLMClientFactory;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.model.ILLMRequest;

public class OllamaService implements ILLMService {
    private final String model;

    public OllamaService(String model) {
        this.model = model;
    }

    @Override
    public CompletionResponse generateResponse(ILLMRequest request) {
        IOllamaClient client = LLMClientFactory.getOllamaClient();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", this.model);
        requestMap.put("messages", request.getPrompt());

        Map<String, Object> optionsMap = new HashMap<>();
        if (null != request.getParameters()) {
            for (Map.Entry<String, Object> entry : request.getParameters().entrySet()) {
                optionsMap.put(entry.getKey(), entry.getValue());
            }
        }
        requestMap.put("options", optionsMap);
        // override disabled features if set dynamically
        requestMap.put("stream", false);

        CompletionResponse response = client.generateCompletion(new CompletionRequest(requestMap, null));
        return response;
    }

    @Override
    public CompletableFuture<CompletionResponse> generateResponseAsync(ILLMRequest request) {
        return CompletableFuture.supplyAsync(() -> generateResponse(request));
    }

    @Override
    public CompletionResponse generateResponse(String inputQuery) {
        IOllamaClient client = LLMClientFactory.getOllamaClient();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", this.model);
        List<Message> prompt = new ArrayList<>();
        prompt.add(new Message(Role.user, inputQuery));
        requestMap.put("messages", prompt);
        // override disabled features if set dynamically
        requestMap.put("stream", false);

        CompletionResponse response = client.generateCompletion(new CompletionRequest(requestMap, null));
        return response;
    }

    @Override
    public CompletableFuture<CompletionResponse> generateResponseAsync(String inputQuery) {
        return CompletableFuture.supplyAsync(() -> generateResponse(inputQuery));
    }
}
