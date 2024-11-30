package com.r7b7.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAIResponse(String id, String model, List<Choice> choices, Usage usage) {}