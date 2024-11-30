package com.r7b7.entity;

public record ErrorResponse(String errorMsg, Exception details) {
    
}
