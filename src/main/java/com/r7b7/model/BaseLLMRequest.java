package com.r7b7.model;

import java.util.List;
import java.util.Map;

import com.r7b7.entity.Message;
import com.r7b7.entity.ToolFunction;

public class BaseLLMRequest implements ILLMRequest {
    private final List<Message> messages;
    private final Map<String, Object> parameters;
    private List<ToolFunction> functions;
    private Object toolChoice;

    public BaseLLMRequest(List<Message> messages, Map<String, Object> parameters, List<ToolFunction> functions,
            Object toolChoice) {
        this.messages = messages;
        this.parameters = parameters;
        this.functions = functions;
        this.toolChoice = toolChoice;
    }

    @Override
    public List<Message> getPrompt() {
        return this.messages;
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    @Override
    public List<ToolFunction> getFunctions() {
        return this.functions;
    }

    @Override
    public Object getToolChoice() {
        return this.toolChoice;
    }
}
