package com.r7b7.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.client.IOpenAIClient;
import com.r7b7.client.factory.LLMClientFactory;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.entity.Tool;
import com.r7b7.model.ILLMRequest;

public class OpenAIService implements ILLMService {
    private final String apiKey;
    private final String model;

    public OpenAIService(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public CompletionResponse generateResponse(ILLMRequest request) {
        IOpenAIClient client = LLMClientFactory.getOpenAIClient();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", this.model);
        requestMap.put("messages", request.getPrompt());
        if (null != request.getFunctions()) {
            List<Tool> tool = request.getFunctions().stream().map(func -> new Tool("function", func)).toList();
            requestMap.put("tools", tool);
        }
        if (null != request.getToolChoice()) {
            requestMap.put("tool_choice", request.getToolChoice());
        }
        if (null != request.getParameters()) {
            for (Map.Entry<String, Object> entry : request.getParameters().entrySet()) {
                requestMap.put(entry.getKey(), entry.getValue());
            }
        }
        // override disabled features if set dynamically
        requestMap.put("stream", false);

        CompletionResponse response = client.generateCompletion(new CompletionRequest(requestMap, this.apiKey));
        return response;
    }

    @Override
    public CompletableFuture<CompletionResponse> generateResponseAsync(ILLMRequest request) {
        return CompletableFuture.supplyAsync(() -> generateResponse(request));
    }

    @Override
    public CompletionResponse generateResponse(String inputQuery) {
        IOpenAIClient client = LLMClientFactory.getOpenAIClient();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", this.model);
        List<Message> prompt = new ArrayList<>();
        prompt.add(new Message(Role.user, inputQuery));
        requestMap.put("messages", prompt);
        
        // override disabled features if set dynamically
        requestMap.put("stream", false);

        CompletionResponse response = client.generateCompletion(new CompletionRequest(requestMap, this.apiKey));
        return response;
    }

    @Override
    public CompletableFuture<CompletionResponse> generateResponseAsync(String inputQuery) {
        return CompletableFuture.supplyAsync(() -> generateResponse(inputQuery));
    }
}
