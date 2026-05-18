package com.r7b7.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;

public class DefaultOllamaClientTest {

    private static final URI TEST_URI = URI.create("http://localhost:11434/api/chat");
    private static final String SUCCESS_BODY =
            "{\"model\":\"test\",\"message\":{\"role\":\"assistant\",\"content\":\"Hi there!\"},"
            + "\"total_duration\":\"5191566416\",\"eval_duration\":\"4799921000\"}";

    @Test
    public void testGenerateCompletion_ValidRequest() {
        DefaultOllamaClient client = new DefaultOllamaClient(
                TEST_URI, StubHttpClient.returning(200, SUCCESS_BODY), null, null);

        CompletionResponse response = client.generateCompletion(buildRequest());

        assertNotNull(response);
        assertNotNull(response.messages());
        assertNull(response.error());
    }

    @Test
    public void testGenerateCompletion_WithoutParams() {
        DefaultOllamaClient client = new DefaultOllamaClient(
                TEST_URI, StubHttpClient.returning(200, SUCCESS_BODY), null, null);

        CompletionResponse response = client.generateCompletion(buildRequest());

        assertNotNull(response);
        assertNull(response.error());
    }

    @Test
    public void testGenerateCompletion_HandleException() {
        DefaultOllamaClient client = new DefaultOllamaClient(
                TEST_URI, StubHttpClient.throwing(new IOException("Mocked IOException")), null, null);

        CompletionResponse response = client.generateCompletion(buildRequest());

        assertNotNull(response);
        assertNotNull(response.error());
    }

    private CompletionRequest buildRequest() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "test-model");
        List<Message> prompt = new ArrayList<>();
        prompt.add(new Message(Role.system, "You are a helpful assistant"));
        requestMap.put("messages", prompt);
        return new CompletionRequest(requestMap, "api-key");
    }
}
