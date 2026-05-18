package com.r7b7.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.llm.LlmClient;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.ILLMRequest;

public interface ILLMService extends LlmClient {

    CompletionResponse generateResponse(ILLMRequest request);

    default CompletionResponse generateResponse(String inputQuery) {
        return generateResponse(new BaseLLMRequest(
                List.of(new Message(Role.user, inputQuery)), null, null, null));
    }

    default CompletionResponse chat(ILLMRequest request) {
        return generateResponse(request);
    }

    default CompletableFuture<CompletionResponse> generateResponseAsync(ILLMRequest request) {
        return CompletableFuture.supplyAsync(() -> generateResponse(request), VIRTUAL_THREAD_EXECUTOR);
    }

    default CompletableFuture<CompletionResponse> generateResponseAsync(String inputQuery) {
        return CompletableFuture.supplyAsync(() -> generateResponse(inputQuery), VIRTUAL_THREAD_EXECUTOR);
    }
}
