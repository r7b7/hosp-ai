package com.r7b7.client;

import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;

public interface GroqClient {
    CompletionResponse generateCompletion(CompletionRequest request);
}
