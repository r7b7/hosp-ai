package com.r7b7.client.factory;

import com.r7b7.client.IAnthropicClient;
import com.r7b7.client.DefaultAnthropicClient;
import com.r7b7.client.DefaultGroqClient;
import com.r7b7.client.DefaultOllamaClient;
import com.r7b7.client.DefaultOpenAIClient;
import com.r7b7.client.IGroqClient;
import com.r7b7.client.IOllamaClient;
import com.r7b7.client.IOpenAIClient;

public class LLMClientFactory {
    private static IAnthropicClient currentAnthropicClient;
    private static IGroqClient currentGroqClient;
    private static IOllamaClient currentOllamaClient;
    private static IOpenAIClient currentOpenAIClient;

    // Open AI Client
    public static IOpenAIClient createDefaultOpenAIClient() {
        DefaultOpenAIClient client = new DefaultOpenAIClient();
        return client;
    }

    public static void setOpenAIClient(IOpenAIClient client) {
        currentOpenAIClient = client;
    }

    public static IOpenAIClient getOpenAIClient() {
        if (null == currentOpenAIClient) {
            currentOpenAIClient = createDefaultOpenAIClient();
        }
        return currentOpenAIClient;
    }

    // Anthropic Client
    public static IAnthropicClient createDefaultAnthropicClient() {
        DefaultAnthropicClient client = new DefaultAnthropicClient();
        return client;
    }

    public static void setAnthropicClient(IAnthropicClient client) {
        currentAnthropicClient = client;
    }

    public static IAnthropicClient getAnthropicClient() {
        if (null == currentAnthropicClient) {
            currentAnthropicClient = createDefaultAnthropicClient();
        }
        return currentAnthropicClient;
    }

    // Groq Client
    public static IGroqClient createDefaultGroqClient() {
        DefaultGroqClient client = new DefaultGroqClient();
        return client;
    }

    public static void setGroqClient(IGroqClient client) {
        currentGroqClient = client;
    }

    public static IGroqClient getGroqClient() {
        if (null == currentGroqClient) {
            currentGroqClient = createDefaultGroqClient();
        }
        return currentGroqClient;
    }

    // Ollama Client
    public static IOllamaClient createDefaultOllamaClient() {
        DefaultOllamaClient client = new DefaultOllamaClient();
        return client;
    }

    public static void setOllamaClient(IOllamaClient client) {
        currentOllamaClient = client;
    }

    public static IOllamaClient getOllamaClient() {
        if (null == currentOllamaClient) {
            currentOllamaClient = createDefaultOllamaClient();
        }
        return currentOllamaClient;
    }
}
