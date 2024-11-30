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
import java.util.List;

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

public class DefaultGroqClientTest {
        @Mock
        HttpClient mockHttpClient;

        @InjectMocks
        private DefaultGroqClient defaultGroqClient;

        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        public void testGenerateCompletion_ValidRequest() throws IOException, InterruptedException {
                try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
                        CompletionRequest request = new CompletionRequest(List.of(new Message(Role.assistant, "Hello")),
                                        null,
                                        "test-model", "api-key");
                        HttpResponse<String> mockResponse = mock(HttpResponse.class);
                        when(mockResponse.statusCode()).thenReturn(200);
                        when(mockResponse.body()).thenReturn(
                                        "{\"id\":\"xxx\",\"model\":\"test\",\"choices\":[{\"index\":0, \"message\":{\"type\": \"assistant\", \"text\": \"Hi there!\"}}]}");
                        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                        .thenReturn(mockResponse);
                        mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

                        CompletionResponse response = defaultGroqClient.generateCompletion(request);

                        assertNotNull(response);
                        assertNotNull(response.messages());
                        assertNull(response.error());
                }
        }

        @Test
        public void testGenerateCompletion_WithoutParams() throws IOException, InterruptedException {
                try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {

                        CompletionRequest request = new CompletionRequest(List.of(new Message(Role.assistant, "Hello")),
                                        null,
                                        "test-model", "api-key");
                        HttpResponse<String> mockResponse = mock(HttpResponse.class);
                        when(mockResponse.statusCode()).thenReturn(200);
                        when(mockResponse.body()).thenReturn(
                                        "{\"id\":\"xxx\",\"model\":\"test\",\"choices\":[{\"index\":0, \"message\":{\"type\": \"assistant\", \"text\": \"Hi there!\"}}]}");
                        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                        .thenReturn(mockResponse);
                        mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

                        CompletionResponse response = defaultGroqClient.generateCompletion(request);

                        assertNotNull(response);
                        assertNull(response.error());
                }
        }

        @Test
        public void testGenerateCompletion_InvalidApiKey() throws IOException, InterruptedException {
                try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {

                        CompletionRequest request = new CompletionRequest(List.of(new Message(Role.assistant, "Hello")),
                                        null,
                                        "test-model", "invalid-api-key");
                        HttpResponse<String> mockResponse = mock(HttpResponse.class);
                        when(mockResponse.statusCode()).thenReturn(401);

                        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                        .thenReturn(mockResponse);
                        mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

                        CompletionResponse response = defaultGroqClient.generateCompletion(request);

                        assertNotNull(response);
                        assertNull(response.messages());
                        assertNotNull(response.error());
                }
        }

        @Test
        public void testGenerateCompletion_HandleException() throws IOException, InterruptedException {
                try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {

                        CompletionRequest request = new CompletionRequest(List.of(new Message(Role.assistant, "Hello")),
                                        null,
                                        "test-model", "invalid-api-key");

                        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                        .thenThrow(new IOException("Mocked IOException"));

                        mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

                        CompletionResponse response = defaultGroqClient.generateCompletion(request);

                        assertNotNull(response);
                        assertNotNull(response.error());
                }
        }
}
