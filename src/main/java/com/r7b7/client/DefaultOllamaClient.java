package com.r7b7.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r7b7.client.model.Message;
import com.r7b7.client.model.OllamaResponse;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultOllamaClient implements OllamaClient {
    private String OLLAMA_API_URL = "http://localhost:11434/api/chat";

    public DefaultOllamaClient() {
    }

    @Override
    public CompletionResponse generateCompletion(CompletionRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", request.model());
            requestMap.put("messages", request.messages());
            requestMap.put("stream", false);

            Map<String, Object> optionsMap = new HashMap<>();
            if (null != request.params() && request.params().get("temperature") != null) {
                optionsMap.put("temperature", request.params().get("temperature"));
            }
            if (null != request.params() && request.params().get("seed") != null) {
                optionsMap.put("seed", request.params().get("seed"));
            }

            requestMap.put("options", optionsMap);
            String jsonRequest = objectMapper.writeValueAsString(requestMap);

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
                return new CompletionResponse(null, response, new ErrorResponse(
                        "Request sent to LLM failed with status code " + response.statusCode(), null));
            }
        } catch (Exception ex) {
            return new CompletionResponse(null, null, new ErrorResponse("Request processing failed", ex));
        }
    }

    private CompletionResponse extractResponseText(String responseBody) {
        List<Message> msgs = null;
        OllamaResponse response = null;
        ErrorResponse error = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(responseBody, OllamaResponse.class);
            msgs = List.of(response.message());
        } catch (Exception ex) {
            error = new ErrorResponse("Exception occurred in extracting response", ex);
        }
        return new CompletionResponse(msgs, response, error);
    }
}
