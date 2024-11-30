package com.r7b7.entity;

import java.util.List;
import java.util.Map;

public record CompletionRequest(List<Message> messages, Map<String, Object> params, String model, String apiKey) {
}
