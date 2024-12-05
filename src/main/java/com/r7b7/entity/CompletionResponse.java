package com.r7b7.entity;

import java.util.List;
import java.util.Map;

public record CompletionResponse (List<com.r7b7.client.model.Message> messages, Map<String, Object> metaData, ErrorResponse error){
    
}
