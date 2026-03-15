package com.r7b7.llm.optimize;

import java.util.List;
import java.util.Map;

import com.r7b7.entity.Message;
import com.r7b7.entity.ToolFunction;
import com.r7b7.model.ILLMRequest;

final class OptimizedRequest implements ILLMRequest {
    private final ILLMRequest delegate;
    private final Map<String, Object> parameters;

    OptimizedRequest(ILLMRequest delegate, Map<String, Object> parameters) {
        this.delegate = delegate;
        this.parameters = parameters;
    }

    @Override
    public List<Message> getPrompt() {
        return delegate.getPrompt();
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public List<ToolFunction> getFunctions() {
        return delegate.getFunctions();
    }

    @Override
    public Object getToolChoice() {
        return delegate.getToolChoice();
    }
}
