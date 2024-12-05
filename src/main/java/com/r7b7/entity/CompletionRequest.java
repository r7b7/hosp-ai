package com.r7b7.entity;

import java.util.Map;

public record CompletionRequest(Map<String, Object> requestBody, String apiKey) {
}
