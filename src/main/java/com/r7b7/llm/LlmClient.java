package com.r7b7.llm;

import java.util.concurrent.CompletableFuture;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.model.ILLMRequest;

public interface LlmClient {
    CompletionResponse chat(ILLMRequest request);

    default CompletableFuture<CompletionResponse> chatAsync(ILLMRequest request) {
        return CompletableFuture.supplyAsync(() -> chat(request));
    }
}
