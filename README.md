# Z.ai Open Platform Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/ai.z.openapi/zai-sdk.svg)](https://search.maven.org/artifact/ai.z.openapi/zai-sdk)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-1.8%2B-orange.svg)](https://www.oracle.com/java/)

[中文文档](README_CN.md) | English

The official Java SDK for Z.ai platforms, providing a unified interface to access powerful AI capabilities including chat completion, embeddings, image generation, audio processing, and more.

## ✨ Features

- 🚀 **Type-safe API**: All interfaces are fully type-encapsulated, no need to consult API documentation
- 🔧 **Easy Integration**: Simple and intuitive API design for quick integration
- ⚡ **High Performance**: Built with modern Java libraries for optimal performance
- 🛡️ **Secure**: Built-in authentication and token management
- 📦 **Lightweight**: Minimal dependencies for easy project integration

## 📦 Installation

### Requirements
- Java 1.8 or higher
- Maven or Gradle
- Not supported on Android platform

### Maven
Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ai.z.openapi</groupId>
    <artifactId>zai-sdk</artifactId>
    <version>0.0.5</version>
</dependency>
```

### Gradle
Add the following dependency to your `build.gradle` (for Groovy DSL):

```groovy
dependencies {
    implementation 'ai.z.openapi:zai-sdk:0.0.5'
}
```

### 📋 Dependencies

This SDK uses the following core dependencies:

| Library   | Version |
|-----------|---------|
| OkHttp3   | 3.14.9  |
| Java JWT  | 4.2.2   |
| Jackson   | 2.19.0  |
| Retrofit2 | 2.12.0  |
| RxJava3   | 3.1.10  |
| SLF4J     | 2.0.17  |

## 🚀 Quick Start

**ZHIPU AI API PATH https://open.bigmodel.cn/api/paas/v4/**

**Z.ai API PATH https://api.z.ai/api/paas/v4/**

### Basic Usage

1. **Create a ZaiClient or ZhipuAiClient** with your API credentials
2. **Access services** through the client
3. **Call API methods** with typed parameters

```java
import ai.z.openapi.ZaiClient;
import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;

// Create client with API key, recommend export the ENV api-key
// export ZAI_API_KEY=your.api.key

// For Z.ai platform https://api.z.ai/api/paas/v4/
ZaiClient client = ZaiClient.builder().build();

// For ZHIPU AI platform https://open.bigmodel.cn/api/paas/v4/
ZhipuAiClient zhipuClient = ZhipuAiClient.builder().build();


// Or set the api-key by code
ZaiClient client = ZaiClient.builder()
        .apiKey("your.api.key")
        .build();

// For ZHIPU AI platform https://open.bigmodel.cn/api/paas/v4/
ZhipuAiClient zhipuClient = ZhipuAiClient.builder().apiKey("your.api.key").build();
```

### Client Configuration

The SDK provides a flexible builder pattern for customizing your client:

```java
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key")
    .baseUrl("https://api.z.ai/api/paas/v4/")
    .enableTokenCache()
    .tokenExpire(3600000) // 1 hour
    .connectionPool(10, 5, TimeUnit.MINUTES)
    .build();
```

## 💡 Examples

### Chat Completion

```java
import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;
import java.util.Arrays;

// Create client
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key")
    .build();

// Create chat request
ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
    .model(Constants.ModelChatGLM4)
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessageRole.USER.value())
            .content("Hello, how are you?")
            .build()
    ))
    .stream(false)
    .temperature(0.7f)
    .maxTokens(1024)
    .build();

// Execute request
ChatCompletionResponse response = client.chat().createChatCompletion(request);

if (response.isSuccess()) {
    String content = response.getData().getChoices().get(0).getMessage().getContent().toString();
    System.out.println("Response: " + content);
} else {
    System.err.println("Error: " + response.getMsg());
}
```

### Streaming Chat

```java
// Create streaming request
ChatCompletionCreateParams streamRequest = ChatCompletionCreateParams.builder()
    .model(Constants.ModelChatGLM4)
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessageRole.USER.value())
            .content("Tell me a story")
            .build()
    ))
    .stream(true) // Enable streaming
    .build();

// Execute streaming request
ChatCompletionResponse response = client.chat().createChatCompletion(streamRequest);

if (response.isSuccess() && response.getFlowable() != null) {
    response.getFlowable().subscribe(
        data -> {
            // Handle streaming chunk
            if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                Delta delta = data.getChoices().get(0).getDelta();
                System.out.print(delta + "\n");
            }
        },
        error -> System.err.println("\nStream error: " + error.getMessage()),
        () -> System.out.println("\nStream completed")
    );
}
```

### Function Calling

```java
// Define function
ChatTool weatherTool = ChatTool.builder()
                .type(ChatToolType.FUNCTION.value())
                .function(ChatFunction.builder()
                        .name("get_weather")
                        .description("Get current weather for a location")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(new HashMap<String, ChatFunctionParameterProperty>() {
                                    {
                                        put("location", ChatFunctionParameterProperty.builder()
                                                .type("string")
                                                .description("City name")
                                                .build());
                                    }
                                })
                                .required(Arrays.asList("location"))
                                .build())
                        .build())
                .build();

// Create request with function
ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
    .model(Constants.ModelChatGLM4)
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessageRole.USER.value())
            .content("What's the weather like in Beijing?")
            .build()
    ))
    .tools(Arrays.asList(weatherTool))
    .toolChoice("auto")
    .build();

ChatCompletionResponse response = client.chat().createChatCompletion(request);
```

### Embeddings

```java
import ai.z.openapi.service.embedding.*;

// Create embedding request
EmbeddingCreateParams request = EmbeddingCreateParams.builder()
    .model(Constants.ModelEmbedding3)
    .input(Arrays.asList("Hello world", "How are you?"))
    .build();

// Execute request
EmbeddingResponse response = client.embeddings().createEmbeddings(request);

if (response.isSuccess()) {
    response.getData().getData().forEach(embedding -> {
        System.out.println("Embedding: " + embedding.getEmbedding());
    });
}
```

### Image Generation

```java
import ai.z.openapi.service.image.*;

// Create image generation request
CreateImageRequest request = CreateImageRequest.builder()
    .model(Constants.ModelCogView3Plus)
    .prompt("A beautiful sunset over mountains")
    .size("1024x1024")
    .build();

// Execute request
ImageResponse response = client.images().createImage(request);

if (response.isSuccess()) {
    response.getData().getData().forEach(image -> {
        System.out.println("Image URL: " + image.getUrl());
    });
}
```

### Spring Boot Integration

```java
@RestController
public class AIController {
    
    private final ZaiClient zaiClient;
    
    public AIController() {
        this.zaiClient = ZaiClient.builder()
            .apiKey("your.api.key")
            .enableTokenCache()
            .build();
    }
    
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
            .model(Constants.ModelChatGLM4)
            .messages(Arrays.asList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(request.getMessage())
                    .build()
            ))
            .build();
            
        ChatCompletionResponse response = zaiClient.chat().createChatCompletion(params);
        
        if (response.isSuccess()) {
            String content = response.getData().getChoices().get(0).getMessage().getContent();
            return ResponseEntity.ok(content);
        } else {
            return ResponseEntity.badRequest().body(response.getMsg());
        }
    }
}
```

## 🔧 Available Services

The ZaiClient provides access to comprehensive AI services:

| Service | Description | Key Features |
|---------|-------------|-------------|
| **Chat** | Text generation and conversation | Streaming, function calling, async support |
| **Embeddings** | Text embeddings generation | Multiple embedding models |
| **Images** | Image generation and processing | CogView models, various sizes |
| **Audio** | Speech synthesis and recognition | Text-to-speech, speech-to-text |
| **Files** | File management and processing | Upload, download, batch processing |
| **Assistants** | AI assistant management | Create, configure, and manage assistants |
| **Agents** | Agent-based completions | Specialized agent interactions |
| **Batch** | Batch processing | Bulk operations |
| **Web Search** | Web search integration | Real-time web information |
| **Videos** | Video processing | Video analysis and generation |

## 📈 Release Notes

For detailed release notes and version history, please see [Release-Note.md](Release-Note.md).

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

We welcome contributions! Please feel free to submit a Pull Request.

## 📞 Support

For questions and support:
- Visit [Z.ai Platform](https://z.ai/)
- Visit [ZHIPU AI Open Platform](http://open.bigmodel.cn/)
- Check our [Architecture Documentation](ARCHITECTURE.md)
