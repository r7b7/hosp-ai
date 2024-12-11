package com.r7b7.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.r7b7.entity.Message;
import com.r7b7.entity.ToolFunction;

public class PromptBuilder {
    private List<Message> messages = new ArrayList<>();
    private Map<String, Object> params = new HashMap<>();
    private List<ToolFunction> functions = new ArrayList<>();
    private Object toolChoice = "none";

    public PromptBuilder addMessage(Message message) {
        messages.add(message);
        return this;
    }

    public PromptBuilder addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public PromptBuilder addTool(ToolFunction function) {
        functions.add(function);
        return this;
    }

    public PromptBuilder addToolChoice(String choice) {
        toolChoice = choice;
        return this;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public PromptEngine build(ILLMService service) {
        return new PromptEngine(service, params, messages);
    }
}
