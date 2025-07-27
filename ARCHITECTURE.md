# Z.ai SDK Java - Architecture Documentation

## Overview

The Z.ai SDK for Java provides a robust, service-oriented architecture designed for seamless integration with Z.ai's suite of AI services. It features a clean separation of concerns, comprehensive configuration management, and robust support for synchronous, asynchronous, and streaming operations. The SDK is built on a client-service pattern and leverages reactive programming with RxJava for handling streams.

## Core Components

### 1. Main Client: `ZaiClient`

The `ZaiClient` class is the central entry point for all interactions with the Z.ai API. It manages service instances and handles the underlying HTTP communication.

```java
public class ZaiClient extends AbstractClientBaseService {
    // Service instances (lazily initialized)
    private ChatService chatService;
    private AgentService agentService;
    private EmbeddingService embeddingService;
    private FileService fileService;
    private AudioService audioService;
    private ImageService imageService;
    private BatchService batchService;
    private FineTuningService fineTuningService;
    private WebSearchService webSearchService;
    private VideosService videosService;
    private KnowledgeService knowledgeService;
    private DocumentService documentService;
    private AssistantService assistantService;

    // Constructor
    public ZaiClient(ZaiConfig config) {
        // Initializes OkHttpClient and Retrofit
    }

    // Public, thread-safe service accessors
    public synchronized ChatService chat() { /* ... */ }
    public synchronized AgentService agents() { /* ... */ }
    public synchronized EmbeddingService embeddings() { /* ... */ }
    // ... other service accessors
}
```

### 2. Configuration: `ZaiConfig`

Configuration is managed through the `ZaiConfig` class, which uses a builder pattern for easy and flexible setup.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZaiConfig {
    private String baseUrl; // API endpoint URL
    private String apiSecretKey; // In format {apiKey}.{apiSecret}
    private String apiKey;
    private String apiSecret;
    private int expireMillis = 30 * 60 * 1000; // JWT token expiration
    private String alg = "HS256"; // JWT algorithm
    private boolean disableTokenCache; // Control for token caching

    // Network settings
    private int connectionPoolMaxIdleConnections = 5;
    private long connectionPoolKeepAliveDuration = 1;
    private TimeUnit connectionPoolTimeUnit = TimeUnit.SECONDS;
    private int requestTimeOut;
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;
    private TimeUnit timeOutTimeUnit;

    private String source_channel;
}
```

**Key Configuration Features**:
- **Authentication**: Supports API key/secret and JWT-based authentication with configurable token caching.
- **Networking**: Fine-grained control over connection pooling and request timeouts.
- **Extensibility**: The builder pattern allows for easy addition of new configuration options.

### 3. Service Abstractions

Services are defined by interfaces (e.g., `ChatService`, `EmbeddingService`) and implemented in corresponding `ServiceImpl` classes. This promotes a consistent, interface-driven design.

```java
// Example: ChatService interface
public interface ChatService {
    ChatCompletionResponse createChatCompletion(ChatCompletionCreateParams request);
    ChatCompletionResponse asyncChatCompletion(ChatCompletionCreateParams request);
    QueryModelResultResponse retrieveAsyncResult(AsyncResultRetrieveParams request);
}

// Implementation pattern
public class ChatServiceImpl implements ChatService {
    private final ZaiClient zAiClient;
    private final ChatApi chatApi;

    public ChatServiceImpl(ZaiClient zAiClient) {
        this.zAiClient = zAiClient;
        this.chatApi = this.zAiClient.retrofit().create(ChatApi.class);
    }

    // Method implementations...
}
```

### 4. Request/Response Models

- **`ClientRequest<T>`**: A marker interface for all request objects, ensuring type safety.
- **`ClientResponse<T>`**: A standard interface for all synchronous responses, providing access to data, status, and error information.
- **`FlowableClientResponse<T>`**: An extension for streaming responses, providing a `Flowable<T>` for reactive stream handling.

## Usage Patterns

### Client Instantiation

**Using the `ZaiConfig` builder (Recommended):**

```java
ZaiConfig config = ZaiConfig.builder()
    .apiSecretKey("your.api.key")
    .baseUrl("https://open.bigmodel.cn/")
    .requestTimeOut(60)
    .timeOutTimeUnit(TimeUnit.SECONDS)
    .build();

ZaiClient client = new ZaiClient(config);
```

### Service Interaction

Access services directly from the `ZaiClient` instance.

```java
// Get the chat service
ChatService chatService = client.chat();

// Build a request
ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
    .model("glm-4")
    .messages(Collections.singletonList(
        ChatMessage.builder().role(ChatMessage.Role.USER).content("Hello!").build()
    ))
    .build();

// Execute the request
ChatCompletionResponse response = chatService.createChatCompletion(request);

if (response.isSuccess()) {
    System.out.println("Response: " + response.getData().getChoices().get(0).getMessage().getContent());
} else {
    System.err.println("Error: " + response.getError().getMessage());
}
```

### Streaming Operations

For streaming, set `stream(true)` in the request and subscribe to the `Flowable`.

```java
ChatCompletionCreateParams streamRequest = request.toBuilder().stream(true).build();
ChatCompletionResponse response = chatService.createChatCompletion(streamRequest);

if (response.isSuccess() && response.getFlowable() != null) {
    response.getFlowable().subscribe(
        data -> System.out.print(data.getChoices().get(0).getDelta().getContent()),
        error -> System.err.println("\nStream error: " + error.getMessage()),
        () -> System.out.println("\nStream complete.")
    );
}
```

## Available Services

The `ZaiClient` provides access to the following services:

- `chat()`: Chat completion and conversational AI.
- `agents()`: Agent-based completions.
- `embeddings()`: Text embedding generation.
- `files()`: File management (upload, download, delete).
- `audio()`: Audio processing (speech-to-text, text-to-speech).
- `images()`: Image generation.
- `batches()`: Batch processing for large-scale jobs.
- `fineTuning()`: Model fine-tuning and management.
- `webSearch()`: Integrated web search capabilities.
- `videos()`: Video processing tasks.
- `knowledge()`: Knowledge base management.
- `documents()`: Document processing and analysis.
- `assistants()`: AI assistant functionalities.

## Error Handling

The SDK provides two primary mechanisms for error handling:

1.  **Response-Level Errors**: Check `response.isSuccess()` and use `response.getError()` to get detailed error information from the API.

    ```java
    if (!response.isSuccess()) {
        ChatError error = response.getError();
        System.err.printf("API Error: [%s] %s%n", error.getCode(), error.getMessage());
    }
    ```

2.  **Exception Handling**: Use a `try-catch` block to handle network issues or unexpected client-side problems, such as `ZAiHttpException`.

    ```java
    try {
        // API call
    } catch (ZAiHttpException e) {
        System.err.printf("HTTP Error: %d - %s%n", e.statusCode, e.getMessage());
    } catch (Exception e) {
        System.err.println("An unexpected error occurred: " + e.getMessage());
    }
    ```

## Best Practices

- **Singleton Client**: For most applications, create a single `ZaiClient` instance and share it to leverage connection pooling.
- **Use Builders**: Always use the builder pattern for creating `ZaiConfig` and request objects.
- **Resource Management**: While `ZaiClient` does not require explicit closing for resource management in typical use cases, ensure your application shuts down gracefully.
- **Secure Key Management**: Store API keys securely using environment variables or a secrets management system. Do not hardcode them in your source code.