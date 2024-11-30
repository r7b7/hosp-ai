package com.r7b7.client.factory;

import com.r7b7.client.DefaultOpenAIClient;
import com.r7b7.client.OpenAIClient;

public class OpenAIClientFactory {
    private static OpenAIClient currentClient;

    public static OpenAIClient createDefaultClient() {
        DefaultOpenAIClient client = new DefaultOpenAIClient();
        return client;
    }

    public static void setClient(OpenAIClient client) {
        currentClient = client;
    }

    public static OpenAIClient getClient() {
        if (null == currentClient) {
            currentClient = createDefaultClient();
        }
        return currentClient;
    }
}
