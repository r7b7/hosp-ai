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

A library designed for quick prototyping with LLMs, and fully compatible with production-ready frameworks like Spring Boot.

## Contributions are Welcome (in need of volunteers)
1. Fork the Repo
2. Create a Branch - name it based on issue-fix, documentation, feature
3. Pull a PR

## Features
1. Support for following LLM providers: OpenAI, Anthropic, Groq, Ollama
2. PromptBuilder to build complex prompts
3. Support to customize default client implementations (Flexible approach for integrating with frameworks like SpringBoot)

### Features in Pipeline
1. Image Support
2. Stream Response
      
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


   
## Working Examples
For working examples and tutorials - visit [Wiki](https://github.com/r7b7/hosp-ai/wiki)
