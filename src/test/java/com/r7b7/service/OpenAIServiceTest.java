package com.r7b7.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.r7b7.client.IOpenAIClient;
import com.r7b7.client.factory.LLMClientFactory;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.ILLMRequest;

public class OpenAIServiceTest {
    @Mock
    private IOpenAIClient mockClient;

    @InjectMocks
    private OpenAIService openAIService;

    private static final String TEST_API_KEY = "test-api-key";
    private static final String TEST_MODEL = "mixtral-8x7b-32768";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        openAIService = new OpenAIService(TEST_API_KEY, TEST_MODEL);
    }

    @Test
    public void testGenerateResponse_Success() {
        try (MockedStatic<LLMClientFactory> mockedStatic = mockStatic(LLMClientFactory.class)) {
            ILLMRequest request = createMockLLMRequest("Test prompt");
            CompletionResponse mockCompletionResponse = createMockCompletionResponse("Test response");
            doReturn(mockCompletionResponse).when(mockClient).generateCompletion(any());
            mockedStatic.when(() -> LLMClientFactory.getOpenAIClient()).thenReturn(mockClient);

            CompletionResponse response = openAIService.generateResponse(request);

            assertNotNull(response);
            assertEquals("test content", response.messages().get(0).content());

            Map<String, Object> metadata = response.metaData();
            assertEquals(TEST_MODEL, metadata.get("model"));
            assertEquals("openai", metadata.get("provider"));

            verify(mockClient).generateCompletion(any(CompletionRequest.class));
        }
    }

    @Test
    public void testGenerateResponse_WithParams_Success() {
        try (MockedStatic<LLMClientFactory> mockedStatic = mockStatic(LLMClientFactory.class)) {

            ILLMRequest request = createMockLLMRequestWithParams("Test prompt");
            CompletionResponse mockCompletionResponse = createMockCompletionResponse("Test response");
            doReturn(mockCompletionResponse).when(mockClient).generateCompletion(any());
            mockedStatic.when(() -> LLMClientFactory.getOpenAIClient()).thenReturn(mockClient);

            CompletionResponse response = openAIService.generateResponse(request);

            assertNotNull(response);
            assertEquals("test content", response.messages().get(0).content());

            Map<String, Object> metadata = response.metaData();
            assertEquals(TEST_MODEL, metadata.get("model"));
            assertEquals("openai", metadata.get("provider"));

            verify(mockClient).generateCompletion(any(CompletionRequest.class));
        }
    }

    @Test
    public void testGenerateResponseWithSingleQuery_Success() {
        try (MockedStatic<LLMClientFactory> mockedStatic = mockStatic(LLMClientFactory.class)) {
            CompletionResponse mockCompletionResponse = createMockCompletionResponse("Test response");
            doReturn(mockCompletionResponse).when(mockClient).generateCompletion(any());
            mockedStatic.when(() -> LLMClientFactory.getOpenAIClient()).thenReturn(mockClient);

            CompletionResponse response = openAIService.generateResponse("single query");

            assertNotNull(response);
            assertEquals("test content", response.messages().get(0).content());

            Map<String, Object> metadata = response.metaData();
            assertEquals(TEST_MODEL, metadata.get("model"));
            assertEquals("openai", metadata.get("provider"));

            verify(mockClient).generateCompletion(any(CompletionRequest.class));
        }
    }

    private ILLMRequest createMockLLMRequest(String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(Role.assistant, "You are a helpful assistant"));
        messages.add(new Message(Role.user, prompt));
        ILLMRequest request = new BaseLLMRequest(messages, null, null, null);
        return request;
    }

    private CompletionResponse createMockCompletionResponse(String content) {
        List<com.r7b7.client.model.Message> messages = new ArrayList<>();
        com.r7b7.client.model.Message msg = new com.r7b7.client.model.Message("user", "test content", null);
        messages.add(msg);
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("model", TEST_MODEL);
        metaData.put("provider", "openai");
        CompletionResponse response = new CompletionResponse(messages, metaData, null);
        return response;
    }

    private ILLMRequest createMockLLMRequestWithParams(String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(Role.system, "You are a helpful assistant"));
        messages.add(new Message(Role.assistant, "You are a helpful assistant"));
        messages.add(new Message(Role.user, prompt));

        Map<String, Object> params = Map.of(
                "temperature", 0.7,
                "max_token", 1000);

        ILLMRequest request = new BaseLLMRequest(messages, params, null, null);
        return request;
    }
}
