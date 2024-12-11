package com.r7b7;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.entity.ToolFunction;
import com.r7b7.service.ILLMService;
import com.r7b7.service.LLMServiceFactory;
import com.r7b7.service.PromptBuilder;
import com.r7b7.service.PromptEngine;

public class App {
    public static void main(String[] args) {
       
             ILLMService service = LLMServiceFactory.createService(Provider.OLLAMA, "mistral");

              Map<String, Object> parameters = getParameters();
        ToolFunction function = new ToolFunction("get_current_weather",
                "Get the current weather in a given location in fahrenheit",
                parameters);

        PromptEngine engine = new PromptBuilder()
                .addMessage(new Message(Role.user, "What's the weather in Chicago today?"))
                .addTool(function)
                .addToolCoice("auto")
                .build(service);

        CompletionResponse response = engine.sendQuery();
        System.out.println(response);
    }

    private static Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");

        Map<String, Object> location = new HashMap<>();
        location.put("type", "string");
        location.put("description", "The city and state");

        Map<String, Object> unit = new HashMap<>();
        unit.put("type", "string");
        unit.put("enum", List.of("celsius", "fahrenheit"));

        parameters.put("properties", Map.of("location",location,"unit", unit));
        parameters.put("required", List.of("location", "unit"));
        return parameters;
    }
}
