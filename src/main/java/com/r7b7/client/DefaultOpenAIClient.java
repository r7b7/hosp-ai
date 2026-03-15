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
import com.r7b7.client.model.Message;
import com.r7b7.client.model.OpenAIResponse;
import com.r7b7.constant.HospAiKeys;
import com.r7b7.config.PropertyConfig;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultOpenAIClient implements IOpenAIClient {
    private final String OPENAI_API_URL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Duration requestTimeout;

    public DefaultOpenAIClient() {
        this(null, null, null, null);
    }

    public DefaultOpenAIClient(URI baseUri, HttpClient httpClient, ObjectMapper objectMapper, Duration requestTimeout) {
        try {
            Properties properties = PropertyConfig.loadConfig();
            URI resolved = baseUri != null ? baseUri : URI.create(properties.getProperty(HospAiKeys.Properties.OPENAI_URL));
            this.OPENAI_API_URL = resolved.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Critical configuration missing: " + HospAiKeys.Properties.OPENAI_URL, ex);
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
                    .uri(URI.create(this.OPENAI_API_URL))
                    .header(HospAiKeys.Headers.CONTENT_TYPE, HospAiKeys.ContentTypes.APPLICATION_JSON)
                    .header(HospAiKeys.Headers.AUTHORIZATION, "Bearer " + request.apiKey())
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
        OpenAIResponse response = null;
        ErrorResponse error = null;
        Map<String, Object> metadata = null;

        try {
            response = objectMapper.readValue(responseBody, OpenAIResponse.class);
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
