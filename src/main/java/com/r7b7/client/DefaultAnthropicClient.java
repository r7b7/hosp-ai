package com.r7b7.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r7b7.client.model.AnthropicResponse;
import com.r7b7.client.model.Message;
import com.r7b7.config.PropertyConfig;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultAnthropicClient implements IAnthropicClient {
    private String ANTHROPIC_API_URL;
    private String ANTHROPIC_VERSION;

    public DefaultAnthropicClient() {
        try {
            Properties properties = PropertyConfig.loadConfig();
            ANTHROPIC_API_URL = properties.getProperty("hospai.anthropic.url");
            ANTHROPIC_VERSION = properties.getProperty("hospai.anthropic.version");
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
                    .uri(URI.create(this.ANTHROPIC_API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", request.apiKey())
                    .header("anthropic-version", ANTHROPIC_VERSION)
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
        AnthropicResponse response = null;
        ErrorResponse error = null;
        Map<String, Object> metadata = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(responseBody, AnthropicResponse.class);
            msgs = response.content().stream().map(content -> new Message(content.type(), content.text())).toList();
            metadata = Map.of(
                    "id", response.id(),
                    "model", response.model(),
                    "provider", "Anthropic",
                    "input_tokens", response.usage().inputTokens(),
                    "output_tokens", response.usage().outputTokens());
        } catch (Exception ex) {
            error = new ErrorResponse("Exception occurred in extracting response", ex);
        }
        return new CompletionResponse(msgs, metadata, error);
    }
}
