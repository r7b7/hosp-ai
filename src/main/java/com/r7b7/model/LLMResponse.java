package com.r7b7.model;

import java.util.Map;

import com.r7b7.entity.CompletionResponse;

public interface LLMResponse {
    CompletionResponse getContent();
    Map<String, Object> getMetadata();
}
