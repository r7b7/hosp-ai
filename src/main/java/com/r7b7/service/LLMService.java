package com.r7b7.service;

import java.util.concurrent.CompletableFuture;

import com.r7b7.model.LLMRequest;
import com.r7b7.model.LLMResponse;

public interface LLMService {
    LLMResponse generateResponse(LLMRequest request);
    CompletableFuture<LLMResponse> generateResponseAsync(LLMRequest request);
}
