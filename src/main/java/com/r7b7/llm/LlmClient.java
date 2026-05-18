package com.r7b7.llm;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.ILLMRequest;

public interface LlmClient {

    Executor VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    CompletionResponse chat(ILLMRequest request);

    default CompletionResponse chat(String query) {
        return chat(new BaseLLMRequest(List.of(new Message(Role.user, query)), null, null, null));
    }

    default CompletableFuture<CompletionResponse> chatAsync(ILLMRequest request) {
        return CompletableFuture.supplyAsync(() -> chat(request), VIRTUAL_THREAD_EXECUTOR);
    }

    default CompletableFuture<CompletionResponse> chatAsync(String query) {
        return CompletableFuture.supplyAsync(() -> chat(query), VIRTUAL_THREAD_EXECUTOR);
    }
}
