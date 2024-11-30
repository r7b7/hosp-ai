package com.r7b7.model;

import java.util.Map;

import com.r7b7.entity.CompletionResponse;

public class BaseLLMResponse implements LLMResponse {
    private final CompletionResponse content;
    private final Map<String, Object> metadata;

    public BaseLLMResponse(CompletionResponse content, Map<String, Object> metadata) {
        this.content = content;
        this.metadata = metadata;
    }

    @Override
    public CompletionResponse getContent() {
        return content;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
