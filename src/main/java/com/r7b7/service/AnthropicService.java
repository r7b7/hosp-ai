package com.r7b7.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.client.IAnthropicClient;
import com.r7b7.client.factory.LLMClientFactory;
import com.r7b7.entity.AnthropicTool;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.entity.Tool;
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
        requestMap.put("model", this.model);
        String systemMessage = getSystemMessage(request);
        if (!StringUtility.isNullOrEmpty(systemMessage)) {
            requestMap.put("system", systemMessage);
        }
        requestMap.put("messages", request.getPrompt());
        if (null != request.getFunctions()) {
            List<AnthropicTool> tool = request.getFunctions().stream().map(func -> new AnthropicTool(func.name(), func.description(), func.parameters())).toList();
            requestMap.put("tools", tool);
        }
        if (null != request.getToolChoice() && request.getToolChoice() instanceof String) {
            requestMap.put("tool_choice", Map.of("type", request.getToolChoice()));
        } else {
            requestMap.put("tool_choice", request.getToolChoice());
        }
        // set mandatory param if not set explicitly
        requestMap.put("max_tokens", 1024);
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
        IAnthropicClient client = LLMClientFactory.getAnthropicClient();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", this.model);
        List<Message> prompt = new ArrayList<>();
        prompt.add(new Message(Role.user, inputQuery));
        requestMap.put("messages", prompt);
        // set mandatory param
        requestMap.put("max_tokens", 1024);
        // override disabled features if set dynamically
        requestMap.put("stream", false);

        CompletionResponse response = client.generateCompletion(new CompletionRequest(requestMap, this.apiKey));
        return response;
    }

    @Override
    public CompletableFuture<CompletionResponse> generateResponseAsync(String inputQuery) {
        return CompletableFuture.supplyAsync(() -> generateResponse(inputQuery));
    }

    private String getSystemMessage(ILLMRequest request) {
        String systemMessage = request.getPrompt().stream()
                .filter(msg -> msg.role() == Role.system)
                .findFirst()
                .map(msg -> {
                    request.getPrompt().remove(msg);
                    return msg.content();
                })
                .orElse(null);
        return systemMessage;
    }
}
