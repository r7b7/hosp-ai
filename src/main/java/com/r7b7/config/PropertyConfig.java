package com.r7b7.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyConfig {
    private static Properties properties;

    public static Properties loadConfig() throws IOException {
        if (null == properties) {
            properties = new Properties();
            try (InputStream input = PropertyConfig.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (input == null) {
                    throw new IOException("application.properties not found on classpath. "
                            + "Add the file or supply explicit URIs via LlmClientConfig.");
                }
                properties.load(input);
            }
        }
        return properties;
    }
}
