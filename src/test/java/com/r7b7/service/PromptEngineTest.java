package com.r7b7.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.model.LLMRequest;
import com.r7b7.model.LLMResponse;

public class PromptEngineTest {
    private LLMService mockLlmService;
    private PromptEngine promptEngine;

    @BeforeEach
    void setUp() {
        mockLlmService = Mockito.mock(LLMService.class);
        promptEngine = new PromptEngine(mockLlmService);
    }

    @Test
    void testGetResponse() {
        String inputQuery = "What is the weather today?";
        String expectedContent = "test content";

        LLMResponse mockResponse = mock(LLMResponse.class);
        when(mockResponse.getContent()).thenReturn(createMockCompletionResponse("Test prompt"));
        when(mockLlmService.generateResponse(any(LLMRequest.class))).thenReturn(mockResponse);

        CompletionResponse response = promptEngine.getResponse(inputQuery);

        assertEquals(expectedContent, response.messages().get(0).content());
        verify(mockLlmService, times(1)).generateResponse(any(LLMRequest.class));
    }

    private CompletionResponse createMockCompletionResponse(String content) {
        List<com.r7b7.client.model.Message> messages = new ArrayList<>();
        com.r7b7.client.model.Message msg = new com.r7b7.client.model.Message("user", "test content");
        messages.add(msg);
        CompletionResponse response = new CompletionResponse(messages, null, null);
        return response;
    }
}
