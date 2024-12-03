package com.r7b7.client.factory;

import com.r7b7.client.AnthropicClient;
import com.r7b7.client.DefaultAnthropicClient;
import com.r7b7.client.DefaultGroqClient;
import com.r7b7.client.DefaultOllamaClient;
import com.r7b7.client.DefaultOpenAIClient;
import com.r7b7.client.GroqClient;
import com.r7b7.client.OllamaClient;
import com.r7b7.client.OpenAIClient;

public class LLMClientFactory {
    private static AnthropicClient currentAnthropicClient;
    private static GroqClient currentGroqClient;
    private static OllamaClient currentOllamaClient;
    private static OpenAIClient currentOpenAIClient;

    // Open AI Client
    public static OpenAIClient createDefaultOpenAIClient() {
        DefaultOpenAIClient client = new DefaultOpenAIClient();
        return client;
    }

    public static void setOpenAIClient(OpenAIClient client) {
        currentOpenAIClient = client;
    }

    public static OpenAIClient getOpenAIClient() {
        if (null == currentOpenAIClient) {
            currentOpenAIClient = createDefaultOpenAIClient();
        }
        return currentOpenAIClient;
    }

    // Anthropic Client
    public static AnthropicClient createDefaultAnthropicClient() {
        DefaultAnthropicClient client = new DefaultAnthropicClient();
        return client;
    }

    public static void setAnthropicClient(AnthropicClient client) {
        currentAnthropicClient = client;
    }

    public static AnthropicClient getAnthropicClient() {
        if (null == currentAnthropicClient) {
            currentAnthropicClient = createDefaultAnthropicClient();
        }
        return currentAnthropicClient;
    }

    // Groq Client
    public static GroqClient createDefaultGroqClient() {
        DefaultGroqClient client = new DefaultGroqClient();
        return client;
    }

    public static void setGroqClient(GroqClient client) {
        currentGroqClient = client;
    }

    public static GroqClient getGroqClient() {
        if (null == currentGroqClient) {
            currentGroqClient = createDefaultGroqClient();
        }
        return currentGroqClient;
    }

    // Ollama Client
    public static OllamaClient createDefaultOllamaClient() {
        DefaultOllamaClient client = new DefaultOllamaClient();
        return client;
    }

    public static void setOllamaClient(OllamaClient client) {
        currentOllamaClient = client;
    }

    public static OllamaClient getOllamaClient() {
        if (null == currentOllamaClient) {
            currentOllamaClient = createDefaultOllamaClient();
        }
        return currentOllamaClient;
    }
}
