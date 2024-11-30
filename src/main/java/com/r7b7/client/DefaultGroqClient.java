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
import com.r7b7.client.model.Message;
import com.r7b7.client.model.OpenAIResponse;
import com.r7b7.entity.CompletionRequest;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.ErrorResponse;

public class DefaultGroqClient implements GroqClient {
    private String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public DefaultGroqClient(){}

    @Override
    public CompletionResponse generateCompletion(CompletionRequest request) {
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
            }
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(this.GROQ_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + request.apiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
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
        OpenAIResponse response = null;
        ErrorResponse error = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(responseBody, OpenAIResponse.class);
            msgs = response.choices().stream()
                    .map(choice -> new Message(choice.message().role(), choice.message().content())).toList();
        } catch (Exception ex) {
            error = new ErrorResponse("Exception occurred in extracting response", ex);
        }
        return new CompletionResponse(msgs, response, error);
    }

}
