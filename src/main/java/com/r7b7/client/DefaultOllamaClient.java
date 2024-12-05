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
import com.r7b7.client.model.OllamaResponse;
import com.r7b7.config.PropertyConfig;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultOllamaClient implements IOllamaClient {
    private String OLLAMA_API_URL;

    public DefaultOllamaClient() {
        try {
            Properties properties = PropertyConfig.loadConfig();
            OLLAMA_API_URL = properties.getProperty("hospai.ollama.url");
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
                    .uri(URI.create(this.OLLAMA_API_URL))
                    .header("Content-Type", "application/json")
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
        OllamaResponse response = null;
        ErrorResponse error = null;
        Map<String, Object> metadata = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(responseBody, OllamaResponse.class);
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
