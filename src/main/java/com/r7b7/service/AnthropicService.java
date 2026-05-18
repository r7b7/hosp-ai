package com.r7b7.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.r7b7.client.LlmHttpClient;
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
    private final LlmHttpClient client;

    public AnthropicService(String apiKey, String model, LlmHttpClient client) {
        this.apiKey = apiKey;
        this.model = model;
        this.client = client;
    }

    @Override
    public CompletionResponse generateResponse(ILLMRequest request) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(HospAiKeys.Json.MODEL, this.model);

        String systemMessage = collectSystemMessages(request.getPrompt());
        if (!StringUtility.isNullOrEmpty(systemMessage)) {
            requestMap.put(HospAiKeys.Json.SYSTEM, systemMessage);
        }
        requestMap.put(HospAiKeys.Json.MESSAGES, getNonSystemMessages(request.getPrompt()));

        if (request.getFunctions() != null && !request.getFunctions().isEmpty()) {
            List<AnthropicTool> tools = request.getFunctions().stream()
                    .map(func -> new AnthropicTool(func.name(), func.description(), func.parameters()))
                    .toList();
            requestMap.put(HospAiKeys.Json.TOOLS, tools);
        }
        if (request.getToolChoice() != null) {
            if (request.getToolChoice() instanceof String) {
                requestMap.put(HospAiKeys.Json.TOOL_CHOICE,
                        Map.of(HospAiKeys.Json.TYPE, request.getToolChoice()));
            } else {
                requestMap.put(HospAiKeys.Json.TOOL_CHOICE, request.getToolChoice());
            }
        }
        if (request.getParameters() != null && !request.getParameters().isEmpty()) {
            requestMap.putAll(request.getParameters());
        }
        requestMap.putIfAbsent(HospAiKeys.Json.MAX_TOKENS, 1024);
        requestMap.putIfAbsent(HospAiKeys.Json.STREAM, false);

        return client.generateCompletion(new CompletionRequest(requestMap, this.apiKey));
    }

    private String collectSystemMessages(List<Message> prompt) {
        return prompt.stream()
                .filter(msg -> msg.role() == Role.system)
                .map(msg -> (String) msg.content())
                .collect(Collectors.joining("\n"));
    }

    private List<Message> getNonSystemMessages(List<Message> prompt) {
        return prompt.stream().filter(msg -> msg.role() != Role.system).toList();
    }
}
