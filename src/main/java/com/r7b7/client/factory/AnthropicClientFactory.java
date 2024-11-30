package com.r7b7.client.factory;

import com.r7b7.client.AnthropicClient;
import com.r7b7.client.DefaultAnthropicClient;

public class AnthropicClientFactory {
    private static AnthropicClient currentClient;

    public static AnthropicClient createDefaultClient() {
        DefaultAnthropicClient client = new DefaultAnthropicClient();
        return client;
    }

    public static void setClient(AnthropicClient client) {
        currentClient = client;
    }

    public static AnthropicClient getClient() {
        if (null == currentClient) {
            currentClient = createDefaultClient();
        }
        return currentClient;
    }
}
