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
import com.r7b7.client.model.OllamaResponse;
import com.r7b7.constant.HospAiKeys;
import com.r7b7.config.PropertyConfig;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultOllamaClient implements IOllamaClient {
    private final String OLLAMA_API_URL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Duration requestTimeout;

    public DefaultOllamaClient() {
        this(null, null, null, null);
    }

    public DefaultOllamaClient(URI baseUri, HttpClient httpClient, ObjectMapper objectMapper, Duration requestTimeout) {
        try {
            Properties properties = PropertyConfig.loadConfig();
            URI resolved = baseUri != null ? baseUri : URI.create(properties.getProperty(HospAiKeys.Properties.OLLAMA_URL));
            this.OLLAMA_API_URL = resolved.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Critical configuration missing: " + HospAiKeys.Properties.OLLAMA_URL, ex);
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
                    .uri(URI.create(this.OLLAMA_API_URL))
                    .header(HospAiKeys.Headers.CONTENT_TYPE, HospAiKeys.ContentTypes.APPLICATION_JSON)
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
        OllamaResponse response = null;
        ErrorResponse error = null;
        Map<String, Object> metadata = null;

        try {
            response = objectMapper.readValue(responseBody, OllamaResponse.class);
            msgs = List.of(response.message());
            metadata = Map.of(
                    "model", response.model(),
                    "provider", "Ollama",
                    "total_duration", response.total_duration(),
                    "eval_duration", response.eval_duration());
        } catch (Exception ex) {
            error = new ErrorResponse("Exception occurred in extracting response", ex);
        }
        return new CompletionResponse(msgs, metadata, error);
    }
}
