package com.r7b7.client.factory;

import com.r7b7.client.DefaultOllamaClient;
import com.r7b7.client.OllamaClient;

public class OllamaClientFactory {
    private static OllamaClient currentClient;

    public static OllamaClient createDefaultClient() {
        DefaultOllamaClient client = new DefaultOllamaClient();
        return client;
    }

    public static void setClient(OllamaClient client) {
        currentClient = client;
    }

    public static OllamaClient getClient() {
        if (null == currentClient) {
            currentClient = createDefaultClient();
        }
        return currentClient;
    }
}
