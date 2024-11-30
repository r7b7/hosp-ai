package com.r7b7.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnthropicResponse(String id, String model, List<Content> content,
        @JsonProperty("usage") AnthroUsage usage) {

}
