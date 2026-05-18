package com.r7b7.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.llm.LlmClient;
import com.r7b7.model.ILLMRequest;

public class PromptEngineTest {
    @Mock
    private LlmClient mockLlmClient;

    private PromptEngine promptEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendQuery_Text_Input() {
        String inputQuery = "What is the weather today?";
        when(mockLlmClient.chat(any(ILLMRequest.class))).thenReturn(createMockResponse());
        when(mockLlmClient.chat(anyString())).thenCallRealMethod();

        promptEngine = new PromptEngine(mockLlmClient);
        CompletionResponse response = promptEngine.sendQuery(inputQuery);

        assertEquals("test content", response.messages().get(0).content());
        verify(mockLlmClient, times(1)).chat(any(ILLMRequest.class));
    }

    @Test
    void testSendQuery_Builder_Input() {
        when(mockLlmClient.chat(any(ILLMRequest.class)))
                .thenReturn(createMockResponse());

        PromptBuilder builder = new PromptBuilder()
                .addMessage(new Message(Role.system, "Give output in consistent format"))
                .addMessage(new Message(Role.user, "what's the stock symbol of ARCHER Aviation?"))
                .addMessage(new Message(Role.assistant, "{\"company\":\"Archer\", \"symbol\":\"ACHR\"}"))
                .addMessage(new Message(Role.user, "what's the stock symbol of Palantir technology?"))
                .addParam("temperature", 0.7)
                .addParam("max_tokens", 150);
        promptEngine = builder.build(mockLlmClient);

        CompletionResponse response = promptEngine.sendQuery();

        assertEquals("test content", response.messages().get(0).content());
        verify(mockLlmClient, times(1)).chat(any(ILLMRequest.class));
    }

    private CompletionResponse createMockResponse() {
        List<com.r7b7.client.model.Message> messages = new ArrayList<>();
        messages.add(new com.r7b7.client.model.Message("user", "test content", null));
        return new CompletionResponse(messages, null, null);
    }
}
