package com.r7b7.model;

import java.util.List;
import java.util.Map;

import com.r7b7.entity.Message;
import com.r7b7.entity.Param;

public interface LLMRequest {
    List<Message> getPrompt();
    Map<Param, Object> getParameters();
}
