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
        this.messages.add(message);
        return this;
    }

    public PromptBuilder addParam(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public PromptBuilder addTool(ToolFunction function) {
        this.functions.add(function);
        return this;
    }

    public PromptBuilder addToolChoice(String choice) {
        this.toolChoice = choice;
        return this;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public PromptEngine build(ILLMService service) {
        return new PromptEngine(service, this.params, this.messages, this.functions, this.toolChoice);
    }
}
