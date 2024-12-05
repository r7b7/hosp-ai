package com.r7b7.model;

import java.util.List;
import java.util.Map;

import com.r7b7.entity.Message;

public interface ILLMRequest {
    List<Message> getPrompt();
    Map<String, Object> getParameters();
}
