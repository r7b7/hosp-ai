# hosp-ai
Universal LLM Provider Connector for Java

![GitHub stars](https://img.shields.io/github/stars/r7b7/hosp-ai?style=social)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
![GitHub release (latest by semver)](https://img.shields.io/github/v/tag/r7b7/hosp-ai)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

## How to Add hosp-ai as a Maven Dependency
1. Add jitpack repository in pom file
    ```bash
    <repositories>
		 <repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		 </repository>
	  </repositories>


2. Add hosp-ai dependency
    ```bash
    <dependency>
			<groupId>com.github.r7b7</groupId>
			<artifactId>hosp-ai</artifactId>
			<version>v1.0.0-alpha.1</version>
		</dependency>


## How to Use in Code

      LLMService service = LLMServiceFactory.createService(Provider.GROQ, "<YOUR_GROQ_API_KEY>", "mixtral-8x7b-32768");
      PromptEngine promptEngine = new PromptEngine(service);
      CompletionResponse response = promptEngine.getResponse(query);

  **Explanation:**
  1. Create an instance of LLMService - Pass provider name along with apikey and model name
  2. Create an instance of PromptEngine and pass the service instance to it
  3. Send the Query

  **How to choose a different  model:**
  1. To use a different model from same provider, just update the model name
     ```bash
     LLMService service = LLMServiceFactory.createService(Provider.GROQ, "<YOUR_GROQ_API_KEY>", "<NEW_MODEL_NAME>");

  2. To use a different model from different provider, update all the params passed to create an instance of LLMService
     ```bash
     LLMService service = LLMServiceFactory.createService(<PROVIDER_NAME>, "<YOUR_GROQ_API_KEY>", "<NEW_MODEL_NAME>");

  3. Rest of the code remains the same.

## Currently Supported Providers and Platforms
1. OpenAI
2. Anthropic
3. Groq
4. Ollama

## Integration with SpringBoot
1. Add Jitpack and hosp-ai maven dependencies as mentioned in the beginning.

  Sample code:
     ```bash
      
      @GetMapping("/prompt")
      public CompletionResponse getChatCompletion(@RequestParam String query){
          LLMService service = LLMServiceFactory.createService(Provider.GROQ, "<YOUR_GROQ_API_KEY>", "mixtral-8x7b-32768");
          PromptEngine promptEngine = new PromptEngine(service);
          CompletionResponse response = promptEngine.getResponse(query);
          return response;
      }
      
   
2. Set values of api-key in yaml or properties file
3. Use a custom WebClient 
   **Doc to be updated**
