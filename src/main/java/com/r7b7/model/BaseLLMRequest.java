package com.r7b7.model;

import java.util.List;
import java.util.Map;

import com.r7b7.entity.Message;
import com.r7b7.entity.Param;

public class BaseLLMRequest implements LLMRequest {
    private final List<Message> messages;
    private final Map<Param, Object> parameters;

    public BaseLLMRequest(List<Message> messages, Map<Param, Object> parameters) {
        this.messages = messages;
        this.parameters = parameters;
    }

    @Override
    public List<Message> getPrompt() {
        return messages;
    }

    @Override
    public Map<Param, Object> getParameters() {
        return parameters;
    }
}
