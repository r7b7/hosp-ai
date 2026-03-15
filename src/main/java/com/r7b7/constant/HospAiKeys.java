package com.r7b7.constant;

public final class HospAiKeys {
    private HospAiKeys() {
    }

    public static final class Json {
        private Json() {
        }

        public static final String MODEL = "model";
        public static final String MESSAGES = "messages";
        public static final String SYSTEM = "system";
        public static final String TOOLS = "tools";
        public static final String TOOL_CHOICE = "tool_choice";
        public static final String TYPE = "type";
        public static final String OPTIONS = "options";
        public static final String MAX_TOKENS = "max_tokens";
        public static final String TEMPERATURE = "temperature";
        public static final String STREAM = "stream";
    }

    public static final class Headers {
        private Headers() {
        }

        public static final String CONTENT_TYPE = "Content-Type";
        public static final String AUTHORIZATION = "Authorization";
        public static final String X_API_KEY = "x-api-key";
        public static final String ANTHROPIC_VERSION = "anthropic-version";
    }

    public static final class ContentTypes {
        private ContentTypes() {
        }

        public static final String APPLICATION_JSON = "application/json";
    }

    public static final class Properties {
        private Properties() {
        }

        public static final String OPENAI_URL = "hospai.openai.url";
        public static final String GROQ_URL = "hospai.groq.url";
        public static final String OLLAMA_URL = "hospai.ollama.url";
        public static final String ANTHROPIC_URL = "hospai.anthropic.url";
        public static final String ANTHROPIC_VERSION = "hospai.anthropic.version";
    }
}
