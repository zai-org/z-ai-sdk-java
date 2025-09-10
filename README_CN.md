# Z.ai Open Platform Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/ai.z.openapi/zai-sdk.svg)](https://search.maven.org/artifact/ai.z.openapi/zai-sdk)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-1.8%2B-orange.svg)](https://www.oracle.com/java/)

[English Readme](README.md)

Z.ai AI 平台官方 Java SDK，提供统一接口访问强大的AI能力，包括对话补全、向量嵌入、图像生成、音频处理等功能。

## ✨ 特性

- 🚀 **类型安全**: 所有接口完全类型封装，无需查阅API文档即可完成接入
- 🔧 **简单易用**: 简洁直观的API设计，快速上手
- ⚡ **高性能**: 基于现代Java库构建，性能优异
- 🛡️ **安全可靠**: 内置身份验证和令牌管理
- 📦 **轻量级**: 最小化依赖，易于项目集成

## 📦 安装

### 环境要求
- Java 1.8 或更高版本
- Maven 或 Gradle
- 尚不支持在 Android 平台运行

### Maven 依赖
在您的 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>ai.z.openapi</groupId>
    <artifactId>zai-sdk</artifactId>
    <version>0.0.5</version>
</dependency>
```

### Gradle 依赖
在您的 `build.gradle` 中添加以下依赖（适用于 Groovy DSL）：

```groovy
dependencies {
    implementation 'ai.z.openapi:zai-sdk:0.0.5'
}
```

### 📋 核心依赖

本SDK使用以下核心依赖库：

| Library   | Version |
|-----------|---------|
| OkHttp3   | 3.14.9  |
| Java JWT  | 4.2.2   |
| Jackson   | 2.19.0  |
| Retrofit2 | 2.12.0  |
| RxJava3   | 3.1.10  |
| SLF4J     | 2.0.17  |

## 🚀 快速开始

**智谱AI API 地址 https://open.bigmodel.cn/api/paas/v4/**

**Z.ai API 地址 https://api.z.ai/api/paas/v4/**

### 基本用法

1. **使用API凭证创建ZaiClient**
2. **通过客户端访问服务**
3. **使用类型化参数调用API方法**

```java
import ai.z.openapi.ZaiClient;
import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;

// 创建客户端 推荐使用环境变量设置API凭证
// export ZAI_API_KEY=your.api.key

// 使用 Z.ai 平台客户端 https://api.z.ai/api/paas/v4/
ZaiClient client = ZaiClient.builder().build();

// 使用 智谱 AI 平台客户端 https://open.bigmodel.cn/api/paas/v4/
ZhipuAiClient zhipuClient = ZhipuAiClient.builder().build();

// 或代码设置凭证
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key")
    .build();

// 或为智谱AI平台 bigmodel.cn 创建客户端
ZhipuAiClient zhipuClient = ZhipuAiClient.builder().apiKey("your.api.key").build();
```

### 客户端配置

SDK提供了灵活的构建器模式来自定义您的客户端：

```java
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key")
    .baseUrl("https://api.z.ai/api/paas/v4/")
    .enableTokenCache()
    .tokenExpire(3600000) // 1小时
    .connectionPool(10, 5, TimeUnit.MINUTES)
    .build();
```

## 💡 使用示例

### 对话补全

```java
import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;
import java.util.Arrays;

// 创建客户端
ZaiClient client = ZaiClient.builder()
    .apiKey("your.api.key")
    .build();

// 创建对话请求
ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
    .model(Constants.ModelChatGLM4)
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessageRole.USER.value())
            .content("你好，你怎么样？")
            .build()
    ))
    .stream(false)
    .temperature(0.7f)
    .maxTokens(1024)
    .build();

// 执行请求
ChatCompletionResponse response = client.chat().createChatCompletion(request);

if (response.isSuccess()) {
    String content = response.getData().getChoices().get(0).getMessage().getContent().toString();
    System.out.println("回复: " + content);
} else {
    System.err.println("错误: " + response.getMsg());
}
```

### 流式对话

```java
// 创建流式请求
ChatCompletionCreateParams streamRequest = ChatCompletionCreateParams.builder()
    .model(Constants.ModelChatGLM4)
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessageRole.USER.value())
            .content("给我讲个故事")
            .build()
    ))
    .stream(true) // 启用流式
    .build();

// 执行流式请求
ChatCompletionResponse response = client.chat().createChatCompletion(streamRequest);

if (response.isSuccess() && response.getFlowable() != null) {
    response.getFlowable().subscribe(
        data -> {
            // 处理流式数据块
            if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                String content = data.getChoices().get(0).getDelta().getContent();
                if (content != null) {
                    System.out.print(content);
                }
            }
        },
        error -> System.err.println("\n流式错误: " + error.getMessage()),
        () -> System.out.println("\n流式完成")
    );
}
```

### 函数调用

```java
// 定义函数
ChatTool weatherTool = ChatTool.builder()
                .type(ChatToolType.FUNCTION.value())
                .function(ChatFunction.builder()
                        .name("get_weather")
                        .description("获取指定地点的当前天气")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(new HashMap<String, ChatFunctionParameterProperty>() {
                                    {
                                        put("location", ChatFunctionParameterProperty.builder()
                                                .type("string")
                                                .description("城市名称")
                                                .build());
                                    }
                                })
                                .required(Arrays.asList("location"))
                                .build())
                        .build())
                .build();

// 创建带函数的请求
ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
    .model(Constants.ModelChatGLM4)
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessageRole.USER.value())
            .content("北京的天气怎么样？")
            .build()
    ))
    .tools(Arrays.asList(weatherTool))
    .toolChoice("auto")
    .build();

ChatCompletionResponse response = client.chat().createChatCompletion(request);
```

### 向量嵌入

```java
import ai.z.openapi.service.embedding.*;

// 创建嵌入请求
EmbeddingCreateParams request = EmbeddingCreateParams.builder()
    .model(Constants.ModelEmbedding3)
    .input(Arrays.asList("你好世界", "你好吗？"))
    .build();

// 执行请求
EmbeddingResponse response = client.embeddings().createEmbeddings(request);

if (response.isSuccess()) {
    response.getData().getData().forEach(embedding -> {
        System.out.println("嵌入向量: " + embedding.getEmbedding());
    });
}
```

### 图像生成

```java
import ai.z.openapi.service.image.*;

// 创建图像生成请求
CreateImageRequest request = CreateImageRequest.builder()
    .model(Constants.ModelCogView3Plus)
    .prompt("山间美丽的日落")
    .size("1024x1024")
    .build();

// 执行请求
ImageResponse response = client.images().createImage(request);

if (response.isSuccess()) {
    response.getData().getData().forEach(image -> {
        System.out.println("图像URL: " + image.getUrl());
    });
}
```

### Spring Boot 集成

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

## 🔧 可用服务

### Chat 服务
- 对话补全（同步/异步）
- 流式对话
- 函数调用
- 多模态对话（文本+图像）

### Embeddings 服务
- 文本向量化
- 批量嵌入
- 多种嵌入模型

### Images 服务
- 文本到图像生成
- 图像编辑
- 图像变体

### Audio 服务
- 语音转文本
- 文本转语音
- 语音翻译

### Files 服务
- 文件上传
- 文件管理
- 文件检索

### Assistants 服务
- AI助手创建
- 助手管理
- 对话线程

### Agents 服务
- 智能代理
- 工作流管理
- 任务执行

### Batch 服务
- 批量处理
- 异步任务
- 结果管理

## 📈 版本更新

详细的版本更新记录和历史信息，请查看 [Release-Note.md](Release-Note.md)。

## 📄 许可证

本项目基于 MIT 许可证开源 - 详情请查看 [LICENSE](LICENSE) 文件。

## 🤝 贡献

欢迎贡献代码！请随时提交 Pull Request。

## 📞 支持

如有问题和技术支持:
- Visit [Z.ai Platform](https://z.ai/)
- Visit [ZHIPU AI Open Platform](http://open.bigmodel.cn/)
- Check our [Architecture Documentation](ARCHITECTURE.md)
