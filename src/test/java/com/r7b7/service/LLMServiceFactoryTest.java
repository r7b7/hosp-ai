package com.r7b7.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.r7b7.entity.Provider;

public class LLMServiceFactoryTest {

    @Test
    public void testIsOllamaServiceCreated() {
        assertEquals(true, 
        LLMServiceFactory.createService(Provider.OLLAMA,null,null) instanceof OllamaService);
    }

    @Test
    public void testIsOpenAIServiceCreated() {
        assertEquals(true, 
        LLMServiceFactory.createService(Provider.OPENAI,null,null) instanceof OpenAIService);
    }

    @Test
    public void testIsAnthropicServiceCreated() {
        assertEquals(true, 
        LLMServiceFactory.createService(Provider.ANTHROPIC,null,null) instanceof AnthropicService);
    }

    @Test
    public void testIsGroqServiceCreated() {
        assertEquals(true, 
        LLMServiceFactory.createService(Provider.GROQ,null,null) instanceof GroqService);
    }
}
