package com.r7b7.client.factory;

import com.r7b7.client.DefaultGroqClient;
import com.r7b7.client.GroqClient;

public class GroqClientFactory {
    private static GroqClient currentClient;

    public static GroqClient createDefaultClient() {
        DefaultGroqClient client = new DefaultGroqClient();
        return client;
    }

    public static void setClient(GroqClient client) {
        currentClient = client;
    }

    public static GroqClient getClient() {
        if (null == currentClient) {
            currentClient = createDefaultClient();
        }
        return currentClient;
    }
}
