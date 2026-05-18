package com.r7b7.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.r7b7.client.LlmHttpClient;
import com.r7b7.constant.HospAiKeys;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Tool;
import com.r7b7.model.ILLMRequest;

public class OpenAIService implements ILLMService {
    private final String apiKey;
    private final String model;
    private final LlmHttpClient client;

    public OpenAIService(String apiKey, String model, LlmHttpClient client) {
        this.apiKey = apiKey;
        this.model = model;
        this.client = client;
    }

    @Override
    public CompletionResponse generateResponse(ILLMRequest request) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(HospAiKeys.Json.MODEL, this.model);
        requestMap.put(HospAiKeys.Json.MESSAGES, request.getPrompt());
        if (request.getFunctions() != null && !request.getFunctions().isEmpty()) {
            List<Tool> tools = request.getFunctions().stream()
                    .map(func -> new Tool("function", func)).toList();
            requestMap.put(HospAiKeys.Json.TOOLS, tools);
        }
        if (request.getToolChoice() != null) {
            requestMap.put(HospAiKeys.Json.TOOL_CHOICE, request.getToolChoice());
        }
        if (request.getParameters() != null && !request.getParameters().isEmpty()) {
            requestMap.putAll(request.getParameters());
        }
        requestMap.putIfAbsent(HospAiKeys.Json.STREAM, false);
        return client.generateCompletion(new CompletionRequest(requestMap, this.apiKey));
    }
}
