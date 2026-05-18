package com.r7b7.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r7b7.client.DefaultAnthropicClient;
import com.r7b7.client.DefaultOllamaClient;
import com.r7b7.client.DefaultOpenAIClient;
import com.r7b7.client.LlmHttpClient;
import com.r7b7.config.PropertyConfig;
import com.r7b7.constant.HospAiKeys;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Provider;
import com.r7b7.llm.exception.LlmConfigurationException;
import com.r7b7.llm.exception.LlmException;
import com.r7b7.model.ILLMRequest;
import com.r7b7.service.ILLMService;
import com.r7b7.service.LLMServiceFactory;

public final class DefaultLlmClient implements LlmClient {
    private static final Logger log = LoggerFactory.getLogger(DefaultLlmClient.class);

    private final LlmClientConfig config;
    private final ILLMService service;

    private DefaultLlmClient(LlmClientConfig config, HttpClient httpClient, ObjectMapper objectMapper) {
        if (config == null) {
            throw new LlmConfigurationException("config must not be null");
        }
        if (config.provider() == null) {
            throw new LlmConfigurationException("provider must not be null");
        }
        if (config.model() == null || config.model().isBlank()) {
            throw new LlmConfigurationException("model must not be null/blank");
        }
        this.config = config;
        this.service = buildService(config, httpClient, objectMapper);
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
            throw new LlmException(msg);
        }
        return response;
    }

    private static ILLMService buildService(LlmClientConfig cfg, HttpClient httpClient, ObjectMapper objectMapper) {
        return switch (cfg.provider()) {
            case OPENAI -> {
                ensureApiKey(cfg);
                yield LLMServiceFactory.createService(cfg.provider(), cfg.apiKey(), cfg.model(),
                        new DefaultOpenAIClient(cfg.baseUri(), "OpenAI", httpClient, objectMapper,
                                cfg.requestTimeout()));
            }
            case GROQ -> {
                ensureApiKey(cfg);
                URI groqUri = resolveUri(cfg.baseUri(), HospAiKeys.Properties.GROQ_URL);
                yield LLMServiceFactory.createService(cfg.provider(), cfg.apiKey(), cfg.model(),
                        new DefaultOpenAIClient(groqUri, "Groq", httpClient, objectMapper, cfg.requestTimeout()));
            }
            case ANTHROPIC -> {
                ensureApiKey(cfg);
                yield LLMServiceFactory.createService(cfg.provider(), cfg.apiKey(), cfg.model(),
                        new DefaultAnthropicClient(cfg.baseUri(), cfg.anthropicVersion(), httpClient,
                                objectMapper, cfg.requestTimeout()));
            }
            case OLLAMA -> LLMServiceFactory.createService(cfg.provider(), cfg.apiKey(), cfg.model(),
                    new DefaultOllamaClient(cfg.baseUri(), httpClient, objectMapper, cfg.requestTimeout()));
            default -> throw new LlmConfigurationException("Unsupported provider: " + cfg.provider());
        };
    }

    private static void ensureApiKey(LlmClientConfig cfg) {
        if (cfg.apiKey() == null || cfg.apiKey().isBlank()) {
            throw new LlmConfigurationException("apiKey must be set for provider " + cfg.provider());
        }
    }

    private static URI resolveUri(URI override, String propertyKey) {
        if (override != null) {
            return override;
        }
        try {
            Properties props = PropertyConfig.loadConfig();
            return URI.create(props.getProperty(propertyKey));
        } catch (Exception e) {
            throw new LlmConfigurationException("Could not load URI from property: " + propertyKey, e);
        }
    }

    public static final class Builder {
        private Provider provider;
        private String apiKey;
        private String model;
        private URI baseUri;
        private String anthropicVersion;
        private Duration requestTimeout = Duration.ofSeconds(60);
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

        public Builder baseUri(URI baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public Builder anthropicVersion(String anthropicVersion) {
            this.anthropicVersion = anthropicVersion;
            return this;
        }

        public Builder requestTimeout(Duration requestTimeout) {
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
            return new DefaultLlmClient(
                    new LlmClientConfig(provider, apiKey, model, baseUri, anthropicVersion, requestTimeout), hc, om);
        }
    }
}
