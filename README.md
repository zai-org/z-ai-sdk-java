# Z.ai Open Platform Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/ai.z.openapi/zai-sdk.svg)](https://search.maven.org/artifact/ai.z.openapi/zai-sdk)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-1.8%2B-orange.svg)](https://www.oracle.com/java/)

[中文文档](README_CN.md) | English

**Not yet released.**

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
    <version>0.0.1</version>
</dependency>
```

### Gradle
Add the following dependency to your `build.gradle` (for Groovy DSL):

```groovy
dependencies {
    implementation 'ai.z:zai-sdk:0.0.1'
}
```

Or `build.gradle.kts` (for Kotlin DSL):

```kotlin
dependencies {
    implementation("ai.z:zai-sdk:0.0.1")
}
```

### 📋 Dependencies

This SDK uses the following core dependencies:

| Library | Version |
|---------|----------|
| OkHttp | 4.12.0 |
| Java JWT | 4.4.0 |
| Jackson | 2.17.2 |
| Retrofit2 | 2.11.0 |
| RxJava | 3.1.8 |
| SLF4J | 2.0.16 |

## 🚀 Quick Start

**ZHIPU AI API PATH https://open.bigmodel.cn/api/paas/v4/**

**Z.ai API PATH https://api.z.ai/api/paas/v4/**

### Basic Usage

1. **Create a ZaiClient** with your API credentials
2. **Access services** through the client
3. **Call API methods** with typed parameters

```java
import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;

// Create client with API key, recommend export the ENV api-key
// export ZAI_API_KEY=your.api.key

// For Z.ai platform https://api.z.ai/api/paas/v4/
ZaiClient client = ZaiClient.builder().build();

// For ZHIPU AI platform https://open.bigmodel.cn/api/paas/v4/
ZaiClient zhipuClient = ZaiClient.builder().ofZHIPU().build();


// Or set the api-key by code
ZaiClient client = ZaiClient.builder()
        .apiKey("your.api.key.your.api.secret")
        .build();

// For ZHIPU AI platform https://open.bigmodel.cn/api/paas/v4/
ZaiClient zhipuClient = ZaiClient.ofZHIPU("your.api.key").build();
```

### Client Configuration

The SDK provides a flexible builder pattern for customizing your client:

```java
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key.your.api.secret")
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
    .apiKey("your.api.key.your.api.secret")
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
    String content = response.getData().getChoices().get(0).getMessage().getContent();
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
                String content = data.getChoices().get(0).getDelta().getContent();
                if (content != null) {
                    System.out.print(content);
                }
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
            .properties(Map.of(
                "location", Map.of(
                    "type", "string",
                    "description", "City name"
                )
            ))
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
EmbeddingResponse response = client.embeddings().create(request);

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
    .quality("standard")
    .n(1)
    .build();

// Execute request
ImageResponse response = client.images().generate(request);

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
            .apiKey("your.api.key.your.api.secret")
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
| **Knowledge** | Knowledge base operations | Document indexing and retrieval |
| **Batch** | Batch processing | Bulk operations |
| **Web Search** | Web search integration | Real-time web information |
| **Videos** | Video processing | Video analysis and generation |

## 🎯 Supported Models

### Text Generation
- `glm-4-plus` - Enhanced GLM-4 with improved capabilities
- `glm-4` - Standard GLM-4 model
- `glm-4-air` - Lightweight version for speed
- `glm-4-flash` - Ultra-fast response model
- `glm-4-0520` - GLM-4 model version 0520
- `glm-4-airx` - Extended Air model with additional features
- `glm-4-long` - Optimized for long-context conversations
- `glm-4-voice` - Specialized for voice interactions
- `glm-4.1v-thinking-flash` - Visual reasoning model with thinking capabilities
- `glm-z1-air` - Optimized for mathematical and logical reasoning
- `glm-z1-airx` - Fastest domestic inference model with 200 tokens/s
- `glm-z1-flash` - Completely free reasoning model service
- `glm-4-air-250414` - Enhanced with reinforcement learning optimization
- `glm-4-flash-250414` - Latest free language model
- `glm-4-flashx` - Enhanced Flash version with ultra-fast inference speed
- `glm-4-9b` - Open-source model with 9 billion parameters
- `glm-4-assistant` - AI assistant for various business scenarios
- `glm-4-alltools` - Agent model for complex task planning and execution
- `chatglm3-6b` - Open-source base model with 6 billion parameters
- `codegeex-4` - Code generation and completion model

### Audio Speech Recognition
- `glm-asr` - Context-aware audio transcription model

### Real-time Interaction
- `glm-realtime-air` - Real-time video call model with cross-modal reasoning
- `glm-realtime-flash` - Fast real-time video call model

### Vision Models
- `glm-4v-plus` - Enhanced vision model
- `glm-4v` - Standard vision model
- `glm-4v-plus-0111` - Variable resolution video and image understanding
- `glm-4v-flash` - Free and powerful image understanding model

### Image Generation
- `cogview-3-plus` - Enhanced image generation
- `cogview-3` - Standard image generation
- `cogview-3-flash` - Free image generation model
- `cogview-4-250304` - Advanced image generation with text capabilities
- `cogview-4` - Advanced image generation for precise and personalized AI image expression

### Video Generation
- `cogvideox` - Video generation from text or images
- `cogvideox-flash` - Free video generation model
- `cogvideox-2` - New video generation model
- `viduq1-text` - High-performance video generation from text input
- `viduq1-image` - Video generation from first frame image and text description
- `viduq1-start-end` - Video generation from first and last frame images
- `vidu2-image` - Enhanced video generation from first frame image and text description
- `vidu2-start-end` - Enhanced video generation from first and last frame images
- `vidu2-reference` - Video generation with reference images of people, objects, etc.

### Embeddings
- `embedding-3` - Latest embedding model
- `embedding-2` - Previous generation embedding

### Specialized
- `charglm-3` - Character interaction model
- `cogtts` - Text-to-speech model
- `rerank` - Text reordering and relevance scoring

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
