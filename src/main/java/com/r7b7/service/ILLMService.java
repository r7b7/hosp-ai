package com.r7b7.service;

import java.util.concurrent.CompletableFuture;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.model.ILLMRequest;

public interface ILLMService {
    CompletionResponse generateResponse(ILLMRequest request);

    CompletionResponse generateResponse(String inputQuery);

    CompletableFuture<CompletionResponse> generateResponseAsync(ILLMRequest request);

    CompletableFuture<CompletionResponse> generateResponseAsync(String inputQuery);
}
