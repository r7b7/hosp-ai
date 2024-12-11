package com.r7b7.entity;

import java.util.Map;

public record AnthropicTool(String name, String description, Map<String, Object> input_schema) {
}
