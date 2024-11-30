package com.r7b7.entity;

import java.util.List;

public record CompletionResponse (List<com.r7b7.client.model.Message> messages, Object completeResponse, ErrorResponse error){
    
}
