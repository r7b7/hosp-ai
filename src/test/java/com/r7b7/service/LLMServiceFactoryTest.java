package com.r7b7.service;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.r7b7.client.LlmHttpClient;
import com.r7b7.entity.Provider;

public class LLMServiceFactoryTest {

    private final LlmHttpClient mockClient = mock(LlmHttpClient.class);

    @Test
    public void testIsOllamaServiceCreated() {
        assertInstanceOf(OllamaService.class,
                LLMServiceFactory.createService(Provider.OLLAMA, null, null, mockClient));
    }

    @Test
    public void testIsOpenAIServiceCreated() {
        assertInstanceOf(OpenAIService.class,
                LLMServiceFactory.createService(Provider.OPENAI, null, null, mockClient));
    }

    @Test
    public void testIsAnthropicServiceCreated() {
        assertInstanceOf(AnthropicService.class,
                LLMServiceFactory.createService(Provider.ANTHROPIC, null, null, mockClient));
    }

    @Test
    public void testIsGroqServiceCreated() {
        assertInstanceOf(OpenAIService.class,
                LLMServiceFactory.createService(Provider.GROQ, null, null, mockClient));
    }
}
