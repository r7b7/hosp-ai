package com.r7b7.llm.exception;

public class LlmConfigurationException extends LlmException {
    public LlmConfigurationException(String message) {
        super(message);
    }

    public LlmConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
