package com.r7b7.llm.exception;

public class LlmRequestException extends LlmException {
    private final int statusCode;
    private final String responseBody;

    public LlmRequestException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public LlmRequestException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.responseBody = null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
