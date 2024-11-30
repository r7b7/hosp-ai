package com.r7b7.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Param;
import com.r7b7.entity.Role;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.LLMRequest;
import com.r7b7.model.LLMResponse;

public class PromptEngine {
    private final LLMService llmService;
    private final Message assistantMessage;
    private final Map<Param, Object> params;

    public PromptEngine(LLMService llmService) {
        this(llmService, new Message(Role.assistant, "You are a helpful assistant"), null);
    }

    public PromptEngine(LLMService llmService, Message assistantMessage, Map<Param, Object> params) {
        this.llmService = llmService;
        this.assistantMessage = assistantMessage;
        this.params = params;
    }

    public CompletionResponse getResponse(String inputQuery) {
        Message userMsg = new Message(Role.user, inputQuery);
        List<Message> messages = List.of(assistantMessage, userMsg);
        LLMRequest request = new BaseLLMRequest(messages, params);
        LLMResponse response = llmService.generateResponse(request);
        return response.getContent();
    }

    public CompletableFuture<CompletionResponse> getResponseAsync(String inputQuery) {
        Message userMsg = new Message(Role.user, inputQuery);
        List<Message> messages = List.of(assistantMessage, userMsg);
        LLMRequest request = new BaseLLMRequest(messages, params);

        return llmService.generateResponseAsync(request)
                .thenApply(LLMResponse::getContent);
    }

}
