package com.r7b7.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ToolFunction;
import com.r7b7.llm.LlmClient;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.ILLMRequest;

public class PromptEngine {
    private final LlmClient llmClient;
    private final Map<String, Object> params;
    private final List<com.r7b7.entity.Message> messages;
    private final List<ToolFunction> functions;
    private final Object toolChoice;

    public PromptEngine(LlmClient llmClient) {
        this(llmClient, null, null, null, null);
    }

    public PromptEngine(LlmClient llmClient, Map<String, Object> params,
            List<com.r7b7.entity.Message> messages, List<ToolFunction> functions, Object toolChoice) {
        this.llmClient = llmClient;
        this.params = params;
        this.messages = messages;
        this.functions = functions;
        this.toolChoice = toolChoice;
    }

    public CompletionResponse sendQuery() {
        ILLMRequest request = new BaseLLMRequest(messages, params, functions, toolChoice);
        return llmClient.chat(request);
    }

    public CompletableFuture<CompletionResponse> sendQueryAsync() {
        ILLMRequest request = new BaseLLMRequest(messages, params, functions, toolChoice);
        return llmClient.chatAsync(request);
    }

    public CompletionResponse sendQuery(String inputQuery) {
        return llmClient.chat(inputQuery);
    }

    public CompletableFuture<CompletionResponse> sendQueryAsync(String inputQuery) {
        return llmClient.chatAsync(inputQuery);
    }
}
