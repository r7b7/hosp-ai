package com.r7b7.model;

import java.util.List;
import java.util.Map;

import com.r7b7.entity.Message;

public class BaseLLMRequest implements ILLMRequest {
    private final List<Message> messages;
    private final Map<String, Object> parameters;

    public BaseLLMRequest(List<Message> messages, Map<String, Object> parameters) {
        this.messages = messages;
        this.parameters = parameters;
    }

    @Override
    public List<Message> getPrompt() {
        return messages;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
