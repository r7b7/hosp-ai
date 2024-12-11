package com.r7b7.entity;

import java.util.Map;

public record ToolFunction(String name, String description, Map<String, Object> parameters) {
}
