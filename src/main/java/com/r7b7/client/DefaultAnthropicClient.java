package com.r7b7.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.r7b7.client.model.AnthropicResponse;
import com.r7b7.client.model.Message;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultAnthropicClient implements AnthropicClient {
    private String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private String ANTHROPIC_VERSION = "2023-06-01";
    private Integer MAX_TOKENS = 1024;

    public DefaultAnthropicClient() {}

    @Override
    public CompletionResponse generateCompletion(CompletionRequest request){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.valueToTree(request.messages());
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", request.model());
            requestBody.set("messages", arrayNode);

            if (null != request.params()) {
                for (Map.Entry<String, Object> entry : request.params().entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        requestBody.put(entry.getKey(), (String) value);
                    } else if (value instanceof Integer) {
                        requestBody.put(entry.getKey(), (Integer) value);
                    }
                }
            } else {
                requestBody.put("max_tokens", MAX_TOKENS);
            }

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(this.ANTHROPIC_API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", request.apiKey())
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return extractResponseText(response.body());
            } else {
                return new CompletionResponse(null, response, new ErrorResponse(
                        "Request sent to LLM failed with status code " + response, null));
            }
        } catch (Exception ex) {
            return new CompletionResponse(null, null, new ErrorResponse("Request processing failed", ex));
        }

    }

    private CompletionResponse extractResponseText(String responseBody) {
        List<Message> msgs = null;
        AnthropicResponse response = null;
        ErrorResponse error = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(responseBody, AnthropicResponse.class);
            msgs = response.content().stream().map(content -> new Message(content.type(), content.text())).toList();
        } catch (Exception ex) {
            error = new ErrorResponse("Exception occurred in extracting response", ex);
        }
        return new CompletionResponse(msgs, response, error);
    }
}
