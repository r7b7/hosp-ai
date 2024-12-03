package com.r7b7;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws FileNotFoundException, IOException {
         Properties properties = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            // Load properties file
            properties.load(input);

            // Access properties
            String propertyValue = properties.getProperty("hospai.openai.url");
            System.out.println("Property value: " + propertyValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
