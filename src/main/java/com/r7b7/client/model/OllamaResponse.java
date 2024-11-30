package com.r7b7.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaResponse(String model, Message message, String total_duration, String eval_duration){
    
}
