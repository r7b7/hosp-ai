package com.r7b7.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r7b7.client.model.Message;
import com.r7b7.client.model.OpenAIResponse;
import com.r7b7.config.PropertyConfig;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultOpenAIClient implements IOpenAIClient {
    private String OPENAI_API_URL;

    public DefaultOpenAIClient() {
        try {
            Properties properties = PropertyConfig.loadConfig();
            OPENAI_API_URL = properties.getProperty("hospai.openai.url");
        } catch (Exception ex) {
            throw new IllegalStateException("Critical configuration missing: CRITICAL_PROPERTY");
        }
    }

    @Override
    public CompletionResponse generateCompletion(CompletionRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(request.requestBody());

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(this.OPENAI_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + request.apiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return extractResponseText(response.body());
            } else {
                return new CompletionResponse(null, null, new ErrorResponse(
                        "Request sent to LLM failed: " + response.statusCode() + response.body(), null));
            }
        } catch (Exception ex) {
            return new CompletionResponse(null, null, new ErrorResponse("Request processing failed", ex));
        }
    }

    private CompletionResponse extractResponseText(String responseBody) {
        List<Message> msgs = null;
        OpenAIResponse response = null;
        ErrorResponse error = null;
        Map<String, Object> metadata = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(responseBody, OpenAIResponse.class);
            msgs = response.choices().stream()
                    .map(choice -> new Message(choice.message().role(), choice.message().content(),
                            choice.message().toolCalls()))
                    .toList();
            metadata = Map.of(
                    "id", response.id(),
                    "model", response.model(),
                    "provider", "OpenAi",
                    "prompt_tokens", response.usage().promptTokens(),
                    "completion_tokens", response.usage().completionTokens(),
                    "total_tokens", response.usage().totalTokens());
        } catch (Exception ex) {
            error = new ErrorResponse("Exception occurred in extracting response", ex);
        }
        return new CompletionResponse(msgs, metadata, error);
    }
}
