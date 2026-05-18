package com.r7b7.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.r7b7.client.LlmHttpClient;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.ILLMRequest;

public class OllamaServiceTest {
    @Mock
    private LlmHttpClient mockClient;

    private OllamaService ollamaService;

    private static final String TEST_MODEL = "gemma2";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ollamaService = new OllamaService(TEST_MODEL, mockClient);
    }

    @Test
    public void testGenerateResponse_Success() {
        ILLMRequest request = createRequest("Test prompt");
        CompletionResponse mockResponse = createMockResponse();
        when(mockClient.generateCompletion(any(CompletionRequest.class))).thenReturn(mockResponse);

        CompletionResponse response = ollamaService.generateResponse(request);

        assertNotNull(response);
        assertEquals("test content", response.messages().get(0).content());
        assertEquals(TEST_MODEL, response.metaData().get("model"));
        verify(mockClient).generateCompletion(any(CompletionRequest.class));
    }

    @Test
    public void testGenerateResponse_WithParams_Success() {
        ILLMRequest request = createRequestWithParams("Test prompt");
        CompletionResponse mockResponse = createMockResponse();
        when(mockClient.generateCompletion(any(CompletionRequest.class))).thenReturn(mockResponse);

        CompletionResponse response = ollamaService.generateResponse(request);

        assertNotNull(response);
        assertEquals("test content", response.messages().get(0).content());
        verify(mockClient).generateCompletion(any(CompletionRequest.class));
    }

    @Test
    public void testGenerateResponseWithSingleQuery_Success() {
        CompletionResponse mockResponse = createMockResponse();
        when(mockClient.generateCompletion(any(CompletionRequest.class))).thenReturn(mockResponse);

        CompletionResponse response = ollamaService.generateResponse("Single Query");

        assertNotNull(response);
        assertEquals("test content", response.messages().get(0).content());
        verify(mockClient).generateCompletion(any(CompletionRequest.class));
    }

    private ILLMRequest createRequest(String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(Role.assistant, "You are a helpful assistant"));
        messages.add(new Message(Role.user, prompt));
        return new BaseLLMRequest(messages, null, null, null);
    }

    private ILLMRequest createRequestWithParams(String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(Role.user, prompt));
        return new BaseLLMRequest(messages, Map.of("temperature", 0.7), null, null);
    }

    private CompletionResponse createMockResponse() {
        List<com.r7b7.client.model.Message> msgs = new ArrayList<>();
        msgs.add(new com.r7b7.client.model.Message("user", "test content", null));
        Map<String, Object> meta = new HashMap<>();
        meta.put("model", TEST_MODEL);
        meta.put("provider", "Ollama");
        return new CompletionResponse(msgs, meta, null);
    }
}
