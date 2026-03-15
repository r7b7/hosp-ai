package com.r7b7.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.client.IAnthropicClient;
import com.r7b7.client.factory.LLMClientFactory;
import com.r7b7.constant.HospAiKeys;
import com.r7b7.entity.AnthropicTool;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.model.ILLMRequest;
import com.r7b7.util.StringUtility;

public class AnthropicService implements ILLMService {
    private final String apiKey;
    private final String model;

    public AnthropicService(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public CompletionResponse generateResponse(ILLMRequest request) {
        IAnthropicClient client = LLMClientFactory.getAnthropicClient();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(HospAiKeys.Json.MODEL, this.model);
        String systemMessage = getSystemMessage(request.getPrompt());
        if (!StringUtility.isNullOrEmpty(systemMessage)) {
            requestMap.put(HospAiKeys.Json.SYSTEM, systemMessage);
        }
        requestMap.put(HospAiKeys.Json.MESSAGES, getNonSystemMessages(request.getPrompt()));
        if (null != request.getFunctions() && !request.getFunctions().isEmpty()) {
            List<AnthropicTool> tool = request.getFunctions().stream()
                    .map(func -> new AnthropicTool(func.name(), func.description(), func.parameters())).toList();
            requestMap.put(HospAiKeys.Json.TOOLS, tool);
        }
        if (null != request.getToolChoice()) {
            if (request.getToolChoice() instanceof String) {
                requestMap.put(HospAiKeys.Json.TOOL_CHOICE, Map.of(HospAiKeys.Json.TYPE, request.getToolChoice()));
            } else {
                requestMap.put(HospAiKeys.Json.TOOL_CHOICE, request.getToolChoice());
            }
        }
        // set mandatory param if not set explicitly
        if (null != request.getParameters() && !request.getParameters().isEmpty()) {
            for (Map.Entry<String, Object> entry : request.getParameters().entrySet()) {
                requestMap.put(entry.getKey(), entry.getValue());
            }
        }
        requestMap.putIfAbsent(HospAiKeys.Json.MAX_TOKENS, 1024);
        // override disabled features if set dynamically
        requestMap.putIfAbsent(HospAiKeys.Json.STREAM, false);

        CompletionResponse response = client.generateCompletion(new CompletionRequest(requestMap, this.apiKey));
        return response;
    }

    @Override
    public CompletableFuture<CompletionResponse> generateResponseAsync(ILLMRequest request) {
        return CompletableFuture.supplyAsync(() -> generateResponse(request));
    }

    @Override
    public CompletionResponse generateResponse(String inputQuery) {
        IAnthropicClient client = LLMClientFactory.getAnthropicClient();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(HospAiKeys.Json.MODEL, this.model);
        List<Message> prompt = new ArrayList<>();
        prompt.add(new Message(Role.user, inputQuery));
        requestMap.put(HospAiKeys.Json.MESSAGES, prompt);
        // set mandatory param
        requestMap.putIfAbsent(HospAiKeys.Json.MAX_TOKENS, 1024);
        // override disabled features if set dynamically
        requestMap.putIfAbsent(HospAiKeys.Json.STREAM, false);

        CompletionResponse response = client.generateCompletion(new CompletionRequest(requestMap, this.apiKey));
        return response;
    }

    @Override
    public CompletableFuture<CompletionResponse> generateResponseAsync(String inputQuery) {
        return CompletableFuture.supplyAsync(() -> generateResponse(inputQuery));
    }

    private String getSystemMessage(List<Message> prompt) {
        return prompt.stream()
                .filter(msg -> msg.role() == Role.system)
                .findFirst()
                .map(msg -> (String) msg.content())
                .orElse(null);
    }

    private List<Message> getNonSystemMessages(List<Message> prompt) {
        return prompt.stream().filter(msg -> msg.role() != Role.system).toList();
    }
}
