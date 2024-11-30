package com.r7b7.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.r7b7.client.GroqClient;
import com.r7b7.client.factory.GroqClientFactory;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Param;
import com.r7b7.entity.Role;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.LLMRequest;
import com.r7b7.model.LLMResponse;

public class GroqServiceTest {
    @Mock
    private GroqClient mockClient;

    @InjectMocks
    private GroqService groqService;

    private static final String TEST_API_KEY = "test-api-key";
    private static final String TEST_MODEL = "mixtral-8x7b-32768";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        groqService = new GroqService(TEST_API_KEY, TEST_MODEL);
    }

    @Test
    public void testGenerateResponse_Success() {
        try (MockedStatic<GroqClientFactory> mockedStatic = mockStatic(GroqClientFactory.class);) {
            LLMRequest request = createMockLLMRequest("Test prompt");
            CompletionResponse mockCompletionResponse = createMockCompletionResponse("Test response");
            doReturn(mockCompletionResponse).when(mockClient).generateCompletion(any());
            mockedStatic.when(() -> GroqClientFactory.getClient()).thenReturn(mockClient);

            LLMResponse response = groqService.generateResponse(request);

            assertNotNull(response);
            assertEquals("test content", response.getContent().messages().get(0).content());

            Map<String, Object> metadata = response.getMetadata();
            assertEquals(TEST_MODEL, metadata.get("model"));
            assertEquals("grok", metadata.get("provider"));

            verify(mockClient).generateCompletion(any(CompletionRequest.class));
        }
    }

    @Test
    public void testGenerateResponse_WithParams_Success() {
        try (MockedStatic<GroqClientFactory> mockedStatic = mockStatic(GroqClientFactory.class);) {
            LLMRequest request = createMockLLMRequestWithParams("Test prompt");
            CompletionResponse mockCompletionResponse = createMockCompletionResponse("Test response");
            doReturn(mockCompletionResponse).when(mockClient).generateCompletion(any());
            mockedStatic.when(() -> GroqClientFactory.getClient()).thenReturn(mockClient);

            LLMResponse response = groqService.generateResponse(request);

            assertNotNull(response);
            assertEquals("test content", response.getContent().messages().get(0).content());

            Map<String, Object> metadata = response.getMetadata();
            assertEquals(TEST_MODEL, metadata.get("model"));
            assertEquals("grok", metadata.get("provider"));

            verify(mockClient).generateCompletion(any(CompletionRequest.class));
        }
    }

    private LLMRequest createMockLLMRequest(String prompt) {
        List<Message> messages = List.of(new Message(Role.assistant, "You are a helpful assistant"),
                new Message(Role.user, prompt));
        LLMRequest request = new BaseLLMRequest(messages, null);
        return request;
    }

    private CompletionResponse createMockCompletionResponse(String content) {
        List<com.r7b7.client.model.Message> messages = new ArrayList<>();
        com.r7b7.client.model.Message msg = new com.r7b7.client.model.Message("user", "test content");
        messages.add(msg);
        CompletionResponse response = new CompletionResponse(messages, null, null);
        return response;
    }

    private LLMRequest createMockLLMRequestWithParams(String prompt) {
        List<Message> messages = List.of(new Message(Role.assistant, "You are a helpful assistant"),
                new Message(Role.user, prompt));
        Map<Param, Object> params = Map.of(
                Param.temperature, 0.7,
                Param.max_token, 1000);

        LLMRequest request = new BaseLLMRequest(messages, params);
        return request;
    }
}
