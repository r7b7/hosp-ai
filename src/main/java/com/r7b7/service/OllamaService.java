package com.r7b7.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.client.IOllamaClient;
import com.r7b7.client.factory.LLMClientFactory;
import com.r7b7.constant.HospAiKeys;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.entity.Tool;
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
        requestMap.put(HospAiKeys.Json.MODEL, this.model);
        requestMap.put(HospAiKeys.Json.MESSAGES, request.getPrompt());
        if (null != request.getFunctions()) {
            List<Tool> tool = request.getFunctions().stream().map(func -> new Tool("function", func)).toList();
            requestMap.put(HospAiKeys.Json.TOOLS, tool);
        }
        if (null != request.getToolChoice()) {
            requestMap.put(HospAiKeys.Json.TOOL_CHOICE, request.getToolChoice());
        }

        Map<String, Object> optionsMap = new HashMap<>();
        if (null != request.getParameters()) {
            for (Map.Entry<String, Object> entry : request.getParameters().entrySet()) {
                optionsMap.put(entry.getKey(), entry.getValue());
            }
        }
        requestMap.put(HospAiKeys.Json.OPTIONS, optionsMap);
        // override disabled features if set dynamically
        requestMap.putIfAbsent(HospAiKeys.Json.STREAM, false);

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
        requestMap.put(HospAiKeys.Json.MODEL, this.model);
        List<Message> prompt = new ArrayList<>();
        prompt.add(new Message(Role.user, inputQuery));
        requestMap.put(HospAiKeys.Json.MESSAGES, prompt);
        // override disabled features if set dynamically
        requestMap.putIfAbsent(HospAiKeys.Json.STREAM, false);

        CompletionResponse response = client.generateCompletion(new CompletionRequest(requestMap, null));
        return response;
    }

    @Override
    public CompletableFuture<CompletionResponse> generateResponseAsync(String inputQuery) {
        return CompletableFuture.supplyAsync(() -> generateResponse(inputQuery));
    }
}
