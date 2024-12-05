package com.r7b7.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.ILLMRequest;

public class PromptEngine {
    private final ILLMService llmService;
    private final Map<String, Object> params;
    private final List<Message> messages;

    public PromptEngine(ILLMService llmService) {
        this(llmService, null, null);
    }

    public PromptEngine(ILLMService llmService, Map<String, Object> params, List<Message> messages) {
        this.llmService = llmService;
        this.params = params;
        this.messages = messages;
    }

    public CompletionResponse sendQuery() {
        ILLMRequest request = new BaseLLMRequest(this.messages, this.params);
        CompletionResponse response = this.llmService.generateResponse(request);
        return response;
    }

    public CompletableFuture<CompletionResponse> sendQueryAsync() {
        ILLMRequest request = new BaseLLMRequest(this.messages, this.params);
        return this.llmService.generateResponseAsync(request);
    }

    public CompletionResponse sendQuery(String inputQuery) {
        CompletionResponse response = this.llmService.generateResponse(inputQuery);
        return response;
    }

    public CompletableFuture<CompletionResponse> sendQueryAsync(String inputQuery) {
        return this.llmService.generateResponseAsync(inputQuery);
    }
}
