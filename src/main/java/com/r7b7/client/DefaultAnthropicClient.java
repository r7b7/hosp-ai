package com.r7b7.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r7b7.client.model.AnthroToolResponse;
import com.r7b7.client.model.AnthropicResponse;
import com.r7b7.client.model.Message;
import com.r7b7.constant.HospAiKeys;
import com.r7b7.config.PropertyConfig;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultAnthropicClient implements IAnthropicClient {
    private final String ANTHROPIC_API_URL;
    private final String ANTHROPIC_VERSION;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Duration requestTimeout;

    public DefaultAnthropicClient() {
        this(null, null, null, null, null);
    }

    public DefaultAnthropicClient(URI baseUri,
            String anthropicVersion,
            HttpClient httpClient,
            ObjectMapper objectMapper,
            Duration requestTimeout) {
        try {
            Properties properties = PropertyConfig.loadConfig();
            URI resolved = baseUri != null ? baseUri : URI.create(properties.getProperty(HospAiKeys.Properties.ANTHROPIC_URL));
            this.ANTHROPIC_API_URL = resolved.toString();
            this.ANTHROPIC_VERSION = anthropicVersion != null ? anthropicVersion
                    : properties.getProperty(HospAiKeys.Properties.ANTHROPIC_VERSION);
        } catch (Exception ex) {
            throw new IllegalStateException("Critical configuration missing: " + HospAiKeys.Properties.ANTHROPIC_URL, ex);
        }
        this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.requestTimeout = requestTimeout;
    }

    @Override
    public CompletionResponse generateCompletion(CompletionRequest request) {
        try {
            String jsonRequest = this.objectMapper.writeValueAsString(request.requestBody());

            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(this.ANTHROPIC_API_URL))
                    .header(HospAiKeys.Headers.CONTENT_TYPE, HospAiKeys.ContentTypes.APPLICATION_JSON)
                    .header(HospAiKeys.Headers.X_API_KEY, request.apiKey())
                    .header(HospAiKeys.Headers.ANTHROPIC_VERSION, ANTHROPIC_VERSION)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest));

            if (requestTimeout != null) {
                httpRequestBuilder.timeout(requestTimeout);
            }

            HttpRequest httpRequest = httpRequestBuilder.build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
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
            response = objectMapper.readValue(responseBody, AnthropicResponse.class);
            final String role = response.role();
            msgs = response.content().stream().map(content -> {
                if(content.type().equalsIgnoreCase("tool_use")){
                    return new Message(role, content.text(), List.of(new AnthroToolResponse(content.type(), content.id(), content.name(), content.input())));
                } else {
                    return new Message(role, content.text(), null);
                }
            }).toList();
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
