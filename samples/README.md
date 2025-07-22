# Z.ai SDK Java Examples

This directory contains various usage examples of the Z.ai SDK Java version, helping developers to quickly get started and integrate.

## 📁 Example Files Description

### 1. ChatCompletionExample.java
**Basic Chat Conversation Example**
- Demonstrates how to create ZaiClient
- Basic chat conversation functionality
- Error handling and response parsing

### 2. StreamingChatExample.java
**Streaming Chat Example**
- Streaming response handling
- Real-time data reception
- RxJava streaming programming

### 3. FunctionCallingExample.java
**Function Calling Example**
- Defining and registering functions
- Function call request handling
- Function execution result processing

### 4. EmbeddingsExample.java
**Text Embedding Example**
- Text vectorization
- Batch text processing
- Vector result analysis

### 5. ImageGenerationExample.java
**Image Generation Example**
- Text-to-image generation
- Image parameter configuration
- Generation result processing

### 6. ClientConfigurationExample.java
**Client Configuration Example**
- Various client configuration methods
- Connection pool and cache configuration
- Platform-specific configuration

### 7. ComprehensiveServicesExample.java
**Comprehensive Services Example**
- Integrated use of multiple services
- Service capability demonstration
- Best practices showcase

### 8. SpringBootIntegrationExample.java
**Spring Boot Integration Example**
- Spring Boot project integration
- RESTful API design
- Dependency injection configuration

## 🚀 Quick Start

### Environment Preparation
1. Java 1.8 or higher
2. Maven or Gradle
3. Z.ai API key

### Setting API Key
Recommended to set API key using environment variables:
```bash
export ZAI_API_KEY=your.api.key.your.api.secret
```

Or set directly in code:
```java
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key.your.api.secret")
    .build();
```

### Running Examples
1. Ensure Z.ai SDK dependency is added
2. Set API key
3. Run the main method of any example class

## 📋 Dependency Configuration

### Maven
```xml
<dependency>
    <groupId>ai.z</groupId>
    <artifactId>zai-sdk</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Gradle
```groovy
dependencies {
    implementation 'ai.z:zai-sdk:0.0.1'
}
```

## 🎯 Supported Models

### Text Generation Models
- `glm-4-plus` - Enhanced GLM-4
- `glm-4` - Standard GLM-4 model
- `glm-4-air` - Lightweight fast version
- `glm-4-flash` - Ultra-fast response model
- `glm-z1-air` - Mathematical logic reasoning optimized
- `codegeex-4` - Code generation model

### Vision Models
- `glm-4v-plus` - Enhanced vision model
- `glm-4v` - Standard vision model
- `glm-4v-flash` - Free image understanding model

### Image Generation Models
- `cogview-3-plus` - Enhanced image generation
- `cogview-3` - Standard image generation
- `cogview-4` - Advanced image generation

### Embedding Models
- `embedding-3` - Latest embedding model
- `embedding-2` - Previous generation embedding model

## 🔧 Common Configurations

### Basic Configuration
```java
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key")
    .build();
```

### Advanced Configuration
```java
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key")
    .baseUrl("https://api.z.ai/api/paas/v4/")
    .enableTokenCache()
    .tokenExpire(3600000) // 1 hour
    .connectionPool(10, 5, TimeUnit.MINUTES)
    .build();
```

### ZHIPU Platform Configuration
```java
ZaiClient zhipuClient = ZaiClient.ofZHIPU("your.api.key").build();
```

## 💡 Best Practices

1. **Security**: Use environment variables to store API keys
2. **Performance**: Enable token caching and connection pooling
3. **Error Handling**: Always check response status and exceptions
4. **Resource Management**: Set reasonable timeouts and connection limits
5. **Logging**: Add appropriate logging for debugging

## 📞 Support

- [Z.ai Platform](https://z.ai/)
- [ZHIPU AI Open Platform](http://open.bigmodel.cn/)
- [Project Documentation](../../../README.md)
- [Architecture Documentation](../../../ARCHITECTURE.md)

## 🤝 Contributing

Welcome to submit Issues and Pull Requests to improve these examples!