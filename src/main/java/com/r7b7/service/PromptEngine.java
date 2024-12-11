package com.r7b7.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.ToolFunction;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.ILLMRequest;

public class PromptEngine {
    private final ILLMService llmService;
    private final Map<String, Object> params;
    private final List<Message> messages;
    private List<ToolFunction> functions = new ArrayList<>();
    private Object toolChoice = "none";

    public PromptEngine(ILLMService llmService) {
        this(llmService, null, null, null, null);
    }

    public PromptEngine(ILLMService llmService, Map<String, Object> params, List<Message> messages, List<ToolFunction> functions, Object toolChoice) {
        this.llmService = llmService;
        this.params = params;
        this.messages = messages;
        this.functions = functions;
        this.toolChoice = toolChoice;
    }

    public CompletionResponse sendQuery() {
        ILLMRequest request = new BaseLLMRequest(this.messages, this.params, this.functions, this.toolChoice);
        CompletionResponse response = this.llmService.generateResponse(request);
        return response;
    }

    public CompletableFuture<CompletionResponse> sendQueryAsync() {
        ILLMRequest request = new BaseLLMRequest(this.messages, this.params, this.functions, this.toolChoice);
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
