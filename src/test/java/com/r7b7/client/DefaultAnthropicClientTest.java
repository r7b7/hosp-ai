package com.r7b7.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;

public class DefaultAnthropicClientTest {
    @Mock
    HttpClient mockHttpClient;

    @InjectMocks
    private DefaultAnthropicClient defaultAnthropicClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testGenerateCompletion_ValidRequest() throws IOException, InterruptedException {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "test-model");
            List<Message> prompt = new ArrayList<>();
            prompt.add(new Message(Role.system, "You are a helpful assistant"));
            requestMap.put("messages", prompt);
            CompletionRequest request = new CompletionRequest(requestMap, "api-key");

            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn("{\"id\": \"msg_01KVZJSawLDxgY8LvDm2w9KP\",\"model\": \"claude-3-5-sonnet-20241022\",\"content\": [{\"type\": \"assistant\", \"text\": \"Hi there!\"}], \"usage\":{\"input_tokens\": 48,\"output_tokens\": 70}}");
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

            CompletionResponse response = defaultAnthropicClient.generateCompletion(request);

            assertNotNull(response);
            assertNotNull(response.messages());
            assertNull(response.error());
        }
    }

    @Test
    public void testGenerateCompletion_WithoutParams() throws IOException, InterruptedException {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "test-model");
            List<Message> prompt = new ArrayList<>();
            prompt.add(new Message(Role.system, "You are a helpful assistant"));
            requestMap.put("messages", prompt);
            CompletionRequest request = new CompletionRequest(requestMap, "api-key");

            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn("{\"id\": \"msg_01KVZJSawLDxgY8LvDm2w9KP\",\"model\": \"claude-3-5-sonnet-20241022\",\"content\": [{\"type\": \"assistant\", \"text\": \"Hi there!\"}], \"usage\":{\"input_tokens\": 48,\"output_tokens\": 70}}");
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

            CompletionResponse response = defaultAnthropicClient.generateCompletion(request);

            assertNotNull(response);
            assertNull(response.error());
        }
    }

    @Test
    public void testGenerateCompletion_InvalidApiKey() throws IOException, InterruptedException {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "test-model");
            List<Message> prompt = new ArrayList<>();
            prompt.add(new Message(Role.system, "You are a helpful assistant"));
            requestMap.put("messages", prompt);
            CompletionRequest request = new CompletionRequest(requestMap, "api-key");

            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(401);

            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

            CompletionResponse response = defaultAnthropicClient.generateCompletion(request);

            assertNotNull(response);
            assertNull(response.messages());
            assertNotNull(response.error());
        }
    }

    @Test
    public void testGenerateCompletion_HandleException() throws IOException, InterruptedException {

        // Arrange
        Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "test-model");
            List<Message> prompt = new ArrayList<>();
            prompt.add(new Message(Role.system, "You are a helpful assistant"));
            requestMap.put("messages", prompt);
            CompletionRequest request = new CompletionRequest(requestMap, "api-key");

        // Mock the HttpClient to throw an exception
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Mocked IOException"));

        CompletionResponse response = defaultAnthropicClient.generateCompletion(request);

        assertNotNull(response);
        assertNotNull(response.error());

    }
}
