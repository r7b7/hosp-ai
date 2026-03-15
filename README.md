![Banner GIF](banner.gif)

# HOSP-AI
Universal LLM Provider Connector for Java

![GitHub stars](https://img.shields.io/github/stars/r7b7/hosp-ai?style=social)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![GitHub release (latest by semver)](https://img.shields.io/github/v/tag/r7b7/hosp-ai)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)
[![Wiki](https://img.shields.io/badge/Documentation-Wiki-blue)](https://github.com/r7b7/hosp-ai/wiki)

## Why HOSP-AI?
When I first started exploring LLMs and Neural Networks in Python, experimenting was easy. But when I switched back to Java—the language I trust for its scalability and performance—I hit a roadblock. There weren’t any simple tools to help me work seamlessly with multiple LLM providers.

This had to be fixed.

The result? Hosp-AI 

Hosp is a short form derived from the Danish word - holdspiller - In English it means "team player". The idea is to create a library that's effective and fits in easily.

A library designed for quick prototyping with LLMs, and fully compatible with production-ready frameworks like Spring Boot.

Thanks to [Adalflow](https://github.com/SylphAI-Inc/AdalFlow) , the inspiration behind building this library.

## Contributions are Welcome 
1. Fork the Repo
2. Create a Branch - name it based on issue-fix, documentation, feature
3. Pull a PR
4. Once Reviewed, PR will be merged by Admin

## Features
1. Following LLM providers are supported currently: OpenAI, Anthropic, Groq, Ollama
2. PromptBuilder to build complex prompts
3. Flexibility to add customized client implementations 
4. Tools(Function Calls) Supported
5. Add Image in Prompt

### Features in Pipeline
1. Stream Response
2. Structured Output
      
## Installation
1. Add jitpack repository in pom file
    ```bash
    <repositories>
     <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
     </repository>
    </repositories>

2. Add hosp-ai dependency (check latest version)
    ```bash
    <dependency>
     <groupId>com.github.r7b7</groupId>
     <artifactId>hosp-ai</artifactId>
     <version>v1.0.0-alpha.2</version>
    </dependency>


## Enterprise Usage (Recommended)

Use the builder-based facade API to configure provider, model, timeouts, and inject shared dependencies like `HttpClient` and `ObjectMapper`.

```java
import java.net.http.HttpClient;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r7b7.entity.Provider;
import com.r7b7.llm.DefaultLlmClient;
import com.r7b7.llm.LlmClient;
import com.r7b7.model.BaseLLMRequest;

// Build your prompt however you prefer
var request = new BaseLLMRequest(promptMessages, Map.of("temperature", 0.2), null, null);

LlmClient client = DefaultLlmClient.builder()
    .provider(Provider.OPENAI)
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .model("gpt-4o-mini")
    .requestTimeout(Duration.ofSeconds(60))
    .httpClient(HttpClient.newHttpClient())
    .objectMapper(new ObjectMapper())
    .build();

var response = client.chat(request);
```

This API throws runtime exceptions under `com.r7b7.llm.exception` when a request fails, which tends to fit enterprise error handling patterns better than propagating errors via response objects.


   
## Working Examples
For working examples and tutorials - visit [Wiki](https://github.com/r7b7/hosp-ai/wiki)
