package com.r7b7.llm;

import java.net.http.HttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r7b7.client.DefaultAnthropicClient;
import com.r7b7.client.DefaultGroqClient;
import com.r7b7.client.DefaultOllamaClient;
import com.r7b7.client.DefaultOpenAIClient;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Provider;
import com.r7b7.llm.exception.LlmConfigurationException;
import com.r7b7.llm.exception.LlmException;
import com.r7b7.llm.exception.LlmRequestException;
import com.r7b7.model.ILLMRequest;
import com.r7b7.client.factory.LLMClientFactory;
import com.r7b7.service.ILLMService;
import com.r7b7.service.LLMServiceFactory;

public final class DefaultLlmClient implements LlmClient {
    private static final Logger log = LoggerFactory.getLogger(DefaultLlmClient.class);

    private final LlmClientConfig config;
    private final ILLMService service;

    private DefaultLlmClient(LlmClientConfig config, HttpClient httpClient, ObjectMapper objectMapper) {
        this.config = config;

        if (config == null) {
            throw new LlmConfigurationException("config must not be null");
        }
        if (config.provider() == null) {
            throw new LlmConfigurationException("provider must not be null");
        }
        if (config.model() == null || config.model().isBlank()) {
            throw new LlmConfigurationException("model must not be null/blank");
        }

        this.service = createServiceWithInjectedProviderClient(config, httpClient, objectMapper);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public CompletionResponse chat(ILLMRequest request) {
        CompletionResponse response = service.generateResponse(request);
        if (response != null && response.error() != null) {
            String msg = response.error().errorMsg();
            Exception details = response.error().details();
            log.debug("LLM request failed (provider={}): {}", config.provider(), msg);
            if (details != null) {
                throw new LlmException(msg, details);
            }
            throw new LlmRequestException(msg, -1, null);
        }
        return response;
    }

    private static ILLMService createServiceWithInjectedProviderClient(
            LlmClientConfig cfg,
            HttpClient httpClient,
            ObjectMapper objectMapper) {
        Provider provider = cfg.provider();
        switch (provider) {
            case OPENAI -> {
                ensureApiKey(cfg);
                LLMClientFactory.setOpenAIClient(
                        new DefaultOpenAIClient(cfg.baseUri(), httpClient, objectMapper, cfg.requestTimeout()));
                return LLMServiceFactory.createService(provider, cfg.apiKey(), cfg.model());
            }
            case GROQ -> {
                ensureApiKey(cfg);
                LLMClientFactory.setGroqClient(
                        new DefaultGroqClient(cfg.baseUri(), httpClient, objectMapper, cfg.requestTimeout()));
                return LLMServiceFactory.createService(provider, cfg.apiKey(), cfg.model());
            }
            case ANTHROPIC -> {
                ensureApiKey(cfg);
                LLMClientFactory.setAnthropicClient(new DefaultAnthropicClient(
                        cfg.baseUri(),
                        cfg.anthropicVersion(),
                        httpClient,
                        objectMapper,
                        cfg.requestTimeout()));
                return LLMServiceFactory.createService(provider, cfg.apiKey(), cfg.model());
            }
            case OLLAMA -> {
                LLMClientFactory.setOllamaClient(
                        new DefaultOllamaClient(cfg.baseUri(), httpClient, objectMapper, cfg.requestTimeout()));
                return LLMServiceFactory.createService(provider, cfg.model());
            }
            default -> throw new LlmConfigurationException("Unsupported provider: " + provider);
        }
    }

    private static void ensureApiKey(LlmClientConfig cfg) {
        if (cfg.apiKey() == null || cfg.apiKey().isBlank()) {
            throw new LlmConfigurationException("apiKey must be set for provider " + cfg.provider());
        }
    }

    public static final class Builder {
        private Provider provider;
        private String apiKey;
        private String model;
        private java.net.URI baseUri;
        private String anthropicVersion;
        private java.time.Duration requestTimeout = java.time.Duration.ofSeconds(60);
        private HttpClient httpClient;
        private ObjectMapper objectMapper;

        public Builder provider(Provider provider) {
            this.provider = provider;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder baseUri(java.net.URI baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public Builder anthropicVersion(String anthropicVersion) {
            this.anthropicVersion = anthropicVersion;
            return this;
        }

        public Builder requestTimeout(java.time.Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public DefaultLlmClient build() {
            HttpClient hc = this.httpClient != null ? this.httpClient : HttpClient.newHttpClient();
            ObjectMapper om = this.objectMapper != null ? this.objectMapper : new ObjectMapper();
            return new DefaultLlmClient(new LlmClientConfig(provider, apiKey, model, baseUri, anthropicVersion, requestTimeout), hc, om);
        }
    }
}
