# Z.ai SDK Java - Architecture Documentation

## Overview

The Z.ai SDK Java provides a service-oriented architecture that offers clean separation of concerns, comprehensive configuration management, and support for both synchronous and streaming operations. The SDK is built around a client-service pattern with reactive programming support.

## Architecture Components

### 1. Core Client Architecture

#### ZAiClient
The main client class that serves as the entry point for all AI services:

```java
public class ZAiClient extends AbstractClientBaseService {
    // Service instances
    private ChatService chatService;
    private AgentService agentService;
    private EmbeddingService embeddingService;
    // ... other services
    
    // Constructor
    public ZAiClient(ZAiConfig config) {
        // Initialize HTTP client and Retrofit
    }
    
    // Service accessors
    public synchronized ChatService chat() { /* ... */ }
    public synchronized AgentService agents() { /* ... */ }
    // ... other service accessors
}
```

#### Base Request and Response Models

**ClientRequest Interface**: Base interface for all service requests
```java
public interface ClientRequest<T> {
    // Marker interface for type safety
}
```

**ClientResponse Interface**: Base interface for all service responses
```java
public interface ClientResponse<T> {
    T getData();
    void setData(T data);
    void setCode(int code);
    void setMsg(String msg);
    void setSuccess(boolean success);
    void setError(ChatError error);
}
```

**FlowableClientResponse Interface**: Extended interface for streaming responses
```java
public interface FlowableClientResponse<T> extends ClientResponse<T> {
    void setFlowable(Flowable<T> stream);
}
```

### 2. Configuration Management

#### ZAiConfig
Main configuration class that contains all SDK settings:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZAiConfig {
    private String baseUrl;
    private String apiSecretKey;
    private String apiKey;
    private String apiSecret;
    private int expireMillis = 30 * 60 * 1000; // 30 minutes
    private String alg = "HS256";
    private boolean disableTokenCache;
    
    // Connection pool settings
    private int connectionPoolMaxIdleConnections = 5;
    private long connectionPoolKeepAliveDuration = 1;
    private TimeUnit connectionPoolTimeUnit = TimeUnit.SECONDS;
    
    // Timeout settings
    private int requestTimeOut;
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;
    private TimeUnit timeOutTimeUnit;
    
    private String source_channel;
}
```

#### Configuration Features

1. **Authentication Settings**
   - API secret key in format `{apiKey}.{apiSecret}`
   - JWT token expiration and algorithm configuration
   - Token caching control

2. **Network Configuration**
   - Base URL for API endpoints
   - Connection pool settings (max idle connections, keep-alive duration)
   - Timeout configurations (request, connect, read, write)

3. **Token Management**
   - JWT token generation and caching
   - Configurable expiration times
   - Option to disable token caching for direct API key usage

### 3. Service Implementations

#### ChatService
Provides chat completion functionality with support for synchronous, asynchronous, and streaming operations:

```java
public interface ChatService {
    /**
     * Creates a chat completion, either streaming or non-streaming based on the request configuration.
     */
    ChatCompletionResponse createChatCompletion(ChatCompletionCreateParams request);
    
    /**
     * Creates an asynchronous chat completion.
     */
    ChatCompletionResponse asyncChatCompletion(ChatCompletionCreateParams request);
    
    /**
     * Retrieves the result of an asynchronous model operation.
     */
    QueryModelResultResponse retrieveAsyncResult(AsyncResultRetrieveParams request);
}
```

#### Service Implementation Pattern
All services follow a consistent implementation pattern:

```java
public class ChatServiceImpl implements ChatService {
    private final ZAiClient zAiClient;
    private final ChatApi chatApi;
    
    public ChatServiceImpl(ZAiClient zAiClient) {
        this.zAiClient = zAiClient;
        this.chatApi = this.zAiClient.retrofit().create(ChatApi.class);
    }
    
    @Override
    public ChatCompletionResponse createChatCompletion(ChatCompletionCreateParams request) {
        // Parameter validation
        String paramMsg = validateParams(request);
        if (StringUtils.isNotEmpty(paramMsg)) {
            return new ChatCompletionResponse(-100, String.format("invalid param: %s", paramMsg));
        }
        
        // Route to streaming or synchronous execution
        if (request.getStream()) {
            return streamChatCompletion(request);
        } else {
            return syncChatCompletion(request);
        }
    }
}
```

## Usage Examples

### Basic Configuration

```java
// Simple configuration with API secret key
ZAiConfig config = new ZAiConfig("your.api.key.your.api.secret");
ZAiClient client = new ZAiClient(config);

// Or using separate API key and secret
ZAiConfig config = new ZAiConfig("your.api.key", "your.api.secret");
ZAiClient client = new ZAiClient(config);
```

### Builder Pattern Configuration

```java
// Using the Builder pattern for advanced configuration
ZAiClient client = new ZAiClient.Builder("your.api.key.your.api.secret")
    .enableTokenCache()
    .networkConfig(
        300,  // request timeout
        100,  // connect timeout
        100,  // read timeout
        100,  // write timeout
        TimeUnit.SECONDS
    )
    .connectionPool(
        10,   // max idle connections
        5,    // keep alive duration
        TimeUnit.MINUTES
    )
    .tokenExpire(3600000) // 1 hour in milliseconds
    .build();
```

### Custom Configuration with ZAiConfig

```java
ZAiConfig config = ZAiConfig.builder()
    .apiSecretKey("your.api.key.your.api.secret")
    .baseUrl("https://custom.api.endpoint")
    .requestTimeOut(60)
    .connectTimeout(30)
    .readTimeout(30)
    .writeTimeout(30)
    .timeOutTimeUnit(TimeUnit.SECONDS)
    .disableTokenCache(false)
    .expireMillis(7200000) // 2 hours
    .connectionPoolMaxIdleConnections(10)
    .connectionPoolKeepAliveDuration(5)
    .connectionPoolTimeUnit(TimeUnit.MINUTES)
    .build();

ZAiClient client = new ZAiClient(config);
```

### Service Usage

```java
// Get service instance
ChatService chatService = client.chat();

// Create chat request
ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
    .model("glm-4")
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessage.Role.USER)
            .content("Hello, world!")
            .build()
    ))
    .stream(false) // Set to true for streaming
    .temperature(0.7f)
    .maxTokens(1024)
    .build();

// Execute request
try {
    ChatCompletionResponse response = chatService.createChatCompletion(request);
    
    if (response.isSuccess()) {
        ModelData data = response.getData();
        if (data != null && data.getChoices() != null && !data.getChoices().isEmpty()) {
            String content = data.getChoices().get(0).getMessage().getContent();
            System.out.println("Response: " + content);
        }
    } else {
        System.err.println("Error: " + response.getMsg());
        if (response.getError() != null) {
            System.err.println("Error details: " + response.getError().getMessage());
        }
    }
} catch (Exception e) {
    System.err.println("Request failed: " + e.getMessage());
}
```

### Streaming Usage

```java
// Create streaming request
ChatCompletionCreateParams streamRequest = ChatCompletionCreateParams.builder()
    .model("glm-4")
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessage.Role.USER)
            .content("Tell me a story")
            .build()
    ))
    .stream(true) // Enable streaming
    .temperature(0.7f)
    .maxTokens(1024)
    .build();

// Execute streaming request
ChatCompletionResponse response = chatService.createChatCompletion(streamRequest);

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
} else {
    System.err.println("Failed to start streaming: " + response.getMsg());
}
```

### Asynchronous Usage

```java
// Create async request
ChatCompletionCreateParams asyncRequest = ChatCompletionCreateParams.builder()
    .model("glm-4")
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessage.Role.USER)
            .content("Generate a long document")
            .build()
    ))
    .build();

// Execute async request
ChatCompletionResponse asyncResponse = chatService.asyncChatCompletion(asyncRequest);

if (asyncResponse.isSuccess()) {
    String taskId = asyncResponse.getData().getTaskId();
    System.out.println("Async task started with ID: " + taskId);
    
    // Poll for results
    AsyncResultRetrieveParams retrieveParams = new AsyncResultRetrieveParams();
    retrieveParams.setId(taskId);
    
    QueryModelResultResponse result = chatService.retrieveAsyncResult(retrieveParams);
    // Handle result...
}
```

## Available Services

The ZAiClient provides access to multiple AI services:

```java
ZAiClient client = new ZAiClient(config);

// Chat completion service
ChatService chatService = client.chat();

// Agent service for agent-based completions
AgentService agentService = client.agents();

// Embedding service for text embeddings
EmbeddingService embeddingService = client.embeddings();

// File management service
FileService fileService = client.files();

// Audio processing service
AudioService audioService = client.audio();

// Image generation service
ImageService imageService = client.images();

// Batch processing service
BatchService batchService = client.batches();

// Fine-tuning service
FineTuningService fineTuningService = client.fineTuning();

// Web search service
WebSearchService webSearchService = client.webSearch();

// Video processing service
VideosService videosService = client.videos();

// Knowledge base service
KnowledgeService knowledgeService = client.knowledge();

// Document management service
DocumentService documentService = client.documents();

// Assistant service
AssistantService assistantService = client.assistants();
```

## Request and Response Models

### Common Request Structure
All requests extend `CommonRequest` which provides common fields:

```java
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonRequest {
    @JsonProperty("request_id")
    private String requestId;
    
    @JsonProperty("user_id")
    private String userId;
    
    // Additional common fields...
}
```

### Chat Request Example
```java
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionCreateParams extends CommonRequest implements ClientRequest<ChatCompletionCreateParams> {
    private String model;
    private List<ChatMessage> messages;
    private Boolean stream;
    private Float temperature;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private List<String> stop;
    private List<ChatTool> tools;
    // ... other fields
}
```

### Response Structure
All responses implement `ClientResponse` or `FlowableClientResponse`:

```java
@Data
public class ChatCompletionResponse implements FlowableClientResponse<ModelData> {
    private int code;
    private String msg;
    private boolean success;
    private ModelData data;
    private Flowable<ModelData> flowable; // For streaming responses
    private ChatError error;
}
```

## Error Handling

### Response Error Handling

```java
ChatCompletionResponse response = chatService.createChatCompletion(request);

// Check if the request was successful
if (!response.isSuccess()) {
    int errorCode = response.getCode();
    String errorMessage = response.getMsg();
    
    System.err.println("Request failed with code: " + errorCode);
    System.err.println("Error message: " + errorMessage);
    
    // Check for detailed error information
    if (response.getError() != null) {
        ChatError error = response.getError();
        System.err.println("Error code: " + error.getCode());
        System.err.println("Error details: " + error.getMessage());
        
        // Handle specific error types
        switch (errorCode) {
            case 400:
                System.err.println("Bad request - check your parameters");
                break;
            case 401:
                System.err.println("Authentication failed - check your API key");
                break;
            case 429:
                System.err.println("Rate limit exceeded - please retry later");
                break;
            case 500:
                System.err.println("Server error - please try again");
                break;
            default:
                System.err.println("Unexpected error occurred");
        }
    }
    return;
}

// Process successful response
ModelData data = response.getData();
if (data != null) {
    // Handle successful response data
    System.out.println("Request completed successfully");
}
```

### Exception Handling

```java
try {
    ChatCompletionResponse response = chatService.createChatCompletion(request);
    // Process response...
} catch (ZAiHttpException e) {
    // Handle HTTP-specific errors
    System.err.println("HTTP Error: " + e.getMessage());
    System.err.println("Status Code: " + e.statusCode);
    System.err.println("Error Code: " + e.code);
} catch (Exception e) {
    // Handle other exceptions
    System.err.println("Unexpected error: " + e.getMessage());
    e.printStackTrace();
}
```

## Extension Points

### Custom Service Implementation

```java
public class CustomService implements AIService<CustomRequest, CustomResponse> {
    @Override
    public CustomResponse execute(CustomRequest request) throws Exception {
        // Implementation
    }
    
    @Override
    public CompletableFuture<CustomResponse> executeAsync(CustomRequest request) {
        // Implementation
    }
    
    @Override
    public Flowable<CustomResponse> executeStream(CustomRequest request) throws Exception {
        // Implementation
    }
    
    @Override
    public void validateRequest(CustomRequest request) throws IllegalArgumentException {
        // Validation logic
    }
    
    @Override
    public String getServiceType() {
        return "CUSTOM_SERVICE";
    }
}

// Register custom service
client.registerService("CUSTOM_SERVICE", new CustomService());
```

### Custom Configuration

```java
// Extend configuration for custom needs
ZAiConfiguration customConfig = ZAiConfigurationBuilder.newBuilder()
    .apiSecretKey("your.api.key")
    .baseUrl("https://custom.endpoint")
    // Add custom settings
    .build();

// Add custom metadata to configuration
customConfig.getAuth().addMetadata("customAuth", "value");
customConfig.getNetwork().addMetadata("customNetwork", "value");
```

## Best Practices

### Configuration Management

```java
// Use builder pattern for configuration
ZAiConfig config = ZAiConfig.builder()
    .apiSecretKey("your.api.key.your.api.secret")
    .baseUrl("https://open.bigmodel.cn/")
    .requestTimeOut(60)
    .connectTimeout(30)
    .readTimeout(30)
    .writeTimeout(30)
    .timeOutTimeUnit(TimeUnit.SECONDS)
    .disableTokenCache(false)
    .expireMillis(3600000) // 1 hour
    .connectionPoolMaxIdleConnections(10)
    .connectionPoolKeepAliveDuration(5)
    .connectionPoolTimeUnit(TimeUnit.MINUTES)
    .build();

ZAiClient client = new ZAiClient(config);
```

### Request Building

```java
// Use builder pattern for creating requests
ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
    .model("glm-4")
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessage.Role.USER)
            .content("Hello, world!")
            .build()
    ))
    .temperature(0.7f)
    .maxTokens(1000)
    .stream(false)
    .build();
```

### Error Handling

```java
// Comprehensive error handling
ChatCompletionResponse response = chatService.createChatCompletion(request);

if (!response.isSuccess()) {
    System.err.println("Request failed: " + response.getMsg());
    if (response.getError() != null) {
        System.err.println("Error details: " + response.getError().getMessage());
    }
    return;
}

// Process successful response
ModelData data = response.getData();
if (data != null && data.getChoices() != null && !data.getChoices().isEmpty()) {
    String content = data.getChoices().get(0).getMessage().getContent();
    System.out.println("Response: " + content);
}
```

### Streaming Best Practices

```java
// Handle streaming responses properly
ChatCompletionCreateParams streamRequest = request.toBuilder()
    .stream(true)
    .build();

ChatCompletionResponse response = chatService.createChatCompletion(streamRequest);

if (response.isSuccess() && response.getFlowable() != null) {
    response.getFlowable()
        .observeOn(Schedulers.io())
        .subscribe(
            data -> {
                // Process each streaming chunk
                if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                    String content = data.getChoices().get(0).getDelta().getContent();
                    if (content != null) {
                        System.out.print(content);
                    }
                }
            },
            error -> {
                System.err.println("Streaming error: " + error.getMessage());
            },
            () -> {
                System.out.println("\nStreaming completed");
            }
        );
}
```

### Resource Management

```java
// Properly manage client lifecycle
try {
    ZAiClient client = new ZAiClient(config);
    ChatService chatService = client.chat();
    
    // Use the service...
    ChatCompletionResponse response = chatService.createChatCompletion(request);
    
} catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
} finally {
    // Clean up resources if needed
}
```

### Asynchronous Processing

```java
// Use async operations for long-running tasks
ChatCompletionResponse asyncResponse = chatService.asyncChatCompletion(request);

if (asyncResponse.isSuccess()) {
    String taskId = asyncResponse.getData().getTaskId();
    
    // Poll for results
    AsyncResultRetrieveParams retrieveParams = new AsyncResultRetrieveParams();
    retrieveParams.setId(taskId);
    
    // Implement polling logic with backoff
    CompletableFuture.supplyAsync(() -> {
        try {
            Thread.sleep(1000); // Wait before polling
            return chatService.retrieveAsyncResult(retrieveParams);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }).thenAccept(result -> {
        // Handle result
        if (result.isSuccess()) {
            System.out.println("Async task completed");
        }
    });
}
```

### Security Best Practices

1. **API Key Management**: Store API keys securely, never hardcode them
2. **Token Caching**: Enable token caching to reduce authentication overhead
3. **Request Validation**: Always validate input parameters
4. **Error Logging**: Log errors but never log sensitive information
5. **Timeout Configuration**: Set appropriate timeouts to prevent hanging requests
6. **Connection Pooling**: Configure connection pools for optimal performance
7. **Rate Limiting**: Implement client-side rate limiting to respect API limits

## Migration Guide

### Upgrading to Latest Version

This guide helps you migrate from older versions of the Z-AI SDK to the current architecture.

#### Key Changes in Current Version

1. **Unified Client Architecture**: All services are now accessed through `ZAiClient`
2. **Improved Configuration**: `ZAiConfig` with builder pattern for better flexibility
3. **Standardized Request/Response**: All requests implement `ClientRequest`, responses implement `ClientResponse`
4. **Enhanced Streaming**: Better support for streaming responses with `FlowableClientResponse`
5. **Comprehensive Service Coverage**: Support for Chat, Agents, Embeddings, Files, Audio, Images, and more

#### Configuration Migration

```java
// If you were using basic configuration
// Old approach (if applicable)
String apiKey = "your-api-key";
String apiSecret = "your-api-secret";

// New approach
ZAiConfig config = ZAiConfig.builder()
    .apiKey(apiKey)
    .apiSecret(apiSecret)
    .baseUrl("https://open.bigmodel.cn/")
    .enableTokenCache(true)
    .tokenExpiredSeconds(3600)
    .build();

ZAiClient client = new ZAiClient(config);
```

#### Service Usage Migration

```java
// Modern service usage
ChatService chatService = client.chat();
EmbeddingService embeddingService = client.embeddings();
FileService fileService = client.files();
AudioService audioService = client.audio();
ImageService imageService = client.images();
// ... and more services
```

#### Request Building Migration

```java
// Use builder pattern for all requests
ChatCompletionCreateParams chatRequest = ChatCompletionCreateParams.builder()
    .model("glm-4")
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role(ChatMessage.Role.USER)
            .content("Hello, world!")
            .build()
    ))
    .temperature(0.7f)
    .maxTokens(1000)
    .build();
```

#### Response Handling Migration

```java
// Standardized response handling
ChatCompletionResponse response = chatService.createChatCompletion(chatRequest);

if (response.isSuccess()) {
    ModelData data = response.getData();
    // Process successful response
} else {
    System.err.println("Error: " + response.getMsg());
    if (response.getError() != null) {
        System.err.println("Details: " + response.getError().getMessage());
    }
}
```

#### Streaming Migration

```java
// Enhanced streaming support
ChatCompletionCreateParams streamRequest = chatRequest.toBuilder()
    .stream(true)
    .build();

ChatCompletionResponse streamResponse = chatService.createChatCompletion(streamRequest);

if (streamResponse.isSuccess() && streamResponse.getFlowable() != null) {
    streamResponse.getFlowable()
        .subscribe(
            data -> {
                // Process streaming data
            },
            error -> {
                // Handle streaming errors
            },
            () -> {
                // Streaming completed
            }
        );
}
```

### Best Practices for Migration

1. **Update Dependencies**: Ensure you're using the latest version of the SDK
2. **Review Configuration**: Update your configuration to use `ZAiConfig.builder()`
3. **Update Service Access**: Use `ZAiClient` to access all services
4. **Standardize Error Handling**: Use the new response structure for error handling
5. **Test Thoroughly**: Test all functionality after migration
6. **Update Documentation**: Update your internal documentation to reflect the new patterns

This architecture provides a solid foundation for future enhancements while maintaining backward compatibility where possible.