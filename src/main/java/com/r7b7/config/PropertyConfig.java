package com.r7b7.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.r7b7.App;

public class PropertyConfig {

    public static Properties loadConfig() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        }
        return properties;
    }
}
