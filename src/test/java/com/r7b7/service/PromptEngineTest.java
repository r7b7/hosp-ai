package com.r7b7.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.model.ILLMRequest;

public class PromptEngineTest {
    @Mock
    private ILLMService mockLlmService;

    @InjectMocks
    private PromptEngine promptEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mockLlmService = Mockito.mock(ILLMService.class);
    }

    @Test
    void testSendQuery_Text_Input() {
        String inputQuery = "What is the weather today?";
        String expectedContent = "test content";

        promptEngine = new PromptEngine(mockLlmService);

        when(mockLlmService.generateResponse(inputQuery))
                .thenReturn(createMockCompletionResponse("Test prompt"));

        CompletionResponse response = promptEngine.sendQuery(inputQuery);

        assertEquals(expectedContent, response.messages().get(0).content());
        verify(mockLlmService, times(1)).generateResponse(inputQuery);
    }

    @Test
    void testSendQuery_Builder_Input() {
        String expectedContent = "test content";

        when(mockLlmService.generateResponse(any(ILLMRequest.class)))
                .thenReturn(createMockCompletionResponse("Test prompt"));

        PromptBuilder builder = new PromptBuilder()
                .addMessage(new Message(Role.system, "Give output in consistent format"))
                .addMessage(new Message(Role.user, "what's the stock symbol of ARCHER Aviation?"))
                .addMessage(new Message(Role.assistant, "{\"company\":\"Archer\", \"symbol\":\"ACHR\"}"))
                .addMessage(new Message(Role.user, "what's the stock symbol of Palantir technology?"))
                .addParam("temperature", 0.7)
                .addParam("max_tokens", 150);
        promptEngine = builder.build(mockLlmService);

        CompletionResponse response = promptEngine.sendQuery();

        assertEquals(expectedContent, response.messages().get(0).content());
        verify(mockLlmService, times(1)).generateResponse(any(ILLMRequest.class));
    }

    private CompletionResponse createMockCompletionResponse(String content) {
        List<com.r7b7.client.model.Message> messages = new ArrayList<>();
        com.r7b7.client.model.Message msg = new com.r7b7.client.model.Message("user", "test content", null);
        messages.add(msg);
        CompletionResponse response = new CompletionResponse(messages, null, null);
        return response;
    }
}
