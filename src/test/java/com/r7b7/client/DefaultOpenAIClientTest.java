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

public class DefaultOpenAIClientTest {

    private static final URI TEST_URI = URI.create("https://api.openai.com/v1/chat/completions");
    private static final String SUCCESS_BODY =
            "{\"id\":\"xxx\",\"model\":\"test\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"Hi there!\"}}],\"usage\":{\"prompt_tokens\":55,\"completion_tokens\":12,\"total_tokens\":67}}";

    @Test
    public void testGenerateCompletion_ValidRequest() {
        DefaultOpenAIClient client = new DefaultOpenAIClient(
                TEST_URI, "OpenAI", StubHttpClient.returning(200, SUCCESS_BODY), null, null);

        CompletionResponse response = client.generateCompletion(buildRequest());

        assertNotNull(response);
        assertNotNull(response.messages());
        assertNull(response.error());
    }

    @Test
    public void testGenerateCompletion_WithoutParams() {
        DefaultOpenAIClient client = new DefaultOpenAIClient(
                TEST_URI, "OpenAI", StubHttpClient.returning(200, SUCCESS_BODY), null, null);

        CompletionResponse response = client.generateCompletion(buildRequest());

        assertNotNull(response);
        assertNull(response.error());
    }

    @Test
    public void testGenerateCompletion_InvalidApiKey() {
        DefaultOpenAIClient client = new DefaultOpenAIClient(
                TEST_URI, "OpenAI", StubHttpClient.returning(401, ""), null, null);

        CompletionResponse response = client.generateCompletion(buildRequest());

        assertNotNull(response);
        assertNull(response.messages());
        assertNotNull(response.error());
    }

    @Test
    public void testGenerateCompletion_HandleException() {
        DefaultOpenAIClient client = new DefaultOpenAIClient(
                TEST_URI, "OpenAI", StubHttpClient.throwing(new IOException("Mocked IOException")), null, null);

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
