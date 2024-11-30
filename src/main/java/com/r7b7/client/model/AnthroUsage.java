package com.r7b7.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnthroUsage(@JsonProperty("input_tokens") String inputTokens,
        @JsonProperty("output_tokens") String outputTokens) {
}
