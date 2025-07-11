package ai.z.openapi;

import ai.z.openapi.service.AbstractClientBaseService;
import ai.z.openapi.service.model.ChatError;
import ai.z.openapi.service.model.ZAiHttpException;
import ai.z.openapi.service.chat.ChatService;
import ai.z.openapi.service.chat.ChatServiceImpl;
import ai.z.openapi.service.agents.AgentService;
import ai.z.openapi.service.agents.AgentServiceImpl;
import ai.z.openapi.service.embedding.EmbeddingService;
import ai.z.openapi.service.embedding.EmbeddingServiceImpl;
import ai.z.openapi.service.file.FileService;
import ai.z.openapi.service.file.FileServiceImpl;
import ai.z.openapi.service.audio.AudioService;
import ai.z.openapi.service.audio.AudioServiceImpl;
import ai.z.openapi.service.image.ImageService;
import ai.z.openapi.service.image.ImageServiceImpl;
import ai.z.openapi.service.batches.BatchService;
import ai.z.openapi.service.batches.BatchServiceImpl;
import ai.z.openapi.service.fine_turning.FineTuningService;
import ai.z.openapi.service.fine_turning.FineTuningServiceImpl;
import ai.z.openapi.service.web_search.WebSearchService;
import ai.z.openapi.service.web_search.WebSearchServiceImpl;
import ai.z.openapi.service.videos.VideosService;
import ai.z.openapi.service.videos.VideosServiceImpl;
import ai.z.openapi.service.knowledge.KnowledgeService;
import ai.z.openapi.service.knowledge.KnowledgeServiceImpl;
import ai.z.openapi.service.document.DocumentService;
import ai.z.openapi.service.document.DocumentServiceImpl;
import ai.z.openapi.service.assistant.AssistantService;
import ai.z.openapi.service.assistant.AssistantServiceImpl;
import ai.z.openapi.core.config.ZAiConfig;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.core.model.FlowableClientResponse;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.OkHttps;
import ai.z.openapi.utils.RequestSupplier;
import ai.z.openapi.utils.StringUtils;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import static ai.z.openapi.Constants.ZHIPU_AI_BASE_URL;
import static ai.z.openapi.Constants.Z_AI_BASE_URL;

/**
 * ZAiClient is the main entry point for interacting with the Z.ai API.
 * This client provides access to various AI services including chat, embeddings, 
 * file operations, audio processing, image generation, and more.
 * 
 * <p>The client supports both Z.ai and ZHIPU AI endpoints and provides 
 * thread-safe lazy initialization of services.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * ZAiClient client = new ZAiClient.Builder("your-api-key")
 *     .networkConfig(30, 10, 30, 30, TimeUnit.SECONDS)
 *     .build();
 * 
 * ChatService chatService = client.chat();
 * // Use the chat service...
 * 
 * client.close(); // Don't forget to close when done
 * }</pre>
 *
 */
public class ZAiClient extends AbstractClientBaseService {

    /** Logger instance for this class */
    private static final Logger logger = LoggerFactory.getLogger(ZAiClient.class);

    /** HTTP client for making network requests */
    private final OkHttpClient httpClient;

    /** Retrofit instance for API communication */
    private Retrofit retrofit;

    // Service instances - lazily initialized for thread safety and performance
    /** Chat service for conversational AI operations */
    private ChatService chatService;
    /** Agent service for AI agent management */
    private AgentService agentService;
    /** Embedding service for text embeddings */
    private EmbeddingService embeddingService;
    /** File service for file operations */
    private FileService fileService;
    /** Audio service for audio processing */
    private AudioService audioService;
    /** Image service for image generation and processing */
    private ImageService imageService;
    /** Batch service for batch processing operations */
    private BatchService batchService;
    /** Fine-tuning service for model customization */
    private FineTuningService fineTuningService;
    /** Web search service for internet search capabilities */
    private WebSearchService webSearchService;
    /** Videos service for video processing */
    private VideosService videosService;
    /** Knowledge service for knowledge base operations */
    private KnowledgeService knowledgeService;
    /** Document service for document processing */
    private DocumentService documentService;
    /** Assistant service for AI assistant functionality */
    private AssistantService assistantService;

    /**
     * Constructs a new ZAiClient with the specified configuration.
     * By default, this client uses the Z.ai OpenAPI endpoint.
     * 
     * @param config the configuration object containing API keys, timeouts, and other settings
     * @throws IllegalArgumentException if config is null or invalid
     */
    public ZAiClient(ZAiConfig config) {
        this.httpClient = OkHttps.create(config);
        this.retrofit = new Retrofit.Builder()
                .baseUrl(StringUtils.isEmpty(config.getBaseUrl()) ? Z_AI_BASE_URL : config.getBaseUrl())
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    // ==================== Service Accessor Methods ====================
    // All service methods use lazy initialization with synchronization for thread safety

    /**
     * Returns the chat service for conversational AI operations.
     * This service handles chat completions, streaming conversations, and related functionality.
     * 
     * @return the ChatService instance (lazily initialized)
     */
    public synchronized ChatService chat() {
        if (chatService == null) {
            this.chatService = new ChatServiceImpl(this);
        }
        return chatService;
    }

    /**
     * Returns the agent service for AI agent management.
     * This service handles agent creation, configuration, and execution.
     * 
     * @return the AgentService instance (lazily initialized)
     */
    public synchronized AgentService agents() {
        if (agentService == null) {
            this.agentService = new AgentServiceImpl(this);
        }
        return agentService;
    }

    /**
     * Returns the embedding service for text embeddings.
     * This service converts text into numerical vector representations.
     * 
     * @return the EmbeddingService instance (lazily initialized)
     */
    public synchronized EmbeddingService embeddings() {
        if (embeddingService == null) {
            this.embeddingService = new EmbeddingServiceImpl(this);
        }
        return embeddingService;
    }

    /**
     * Returns the file service for file operations.
     * This service handles file uploads, downloads, and management.
     * 
     * @return the FileService instance (lazily initialized)
     */
    public synchronized FileService files() {
        if (fileService == null) {
            this.fileService = new FileServiceImpl(this);
        }
        return fileService;
    }

    /**
     * Returns the audio service for audio processing.
     * This service handles speech-to-text, text-to-speech, and audio analysis.
     * 
     * @return the AudioService instance (lazily initialized)
     */
    public synchronized AudioService audio() {
        if (audioService == null) {
            this.audioService = new AudioServiceImpl(this);
        }
        return audioService;
    }

    /**
     * Returns the image service for image generation and processing.
     * This service handles image creation, editing, and analysis.
     * 
     * @return the ImageService instance (lazily initialized)
     */
    public synchronized ImageService images() {
        if (imageService == null) {
            this.imageService = new ImageServiceImpl(this);
        }
        return imageService;
    }

    /**
     * Returns the batch service for batch processing operations.
     * This service handles large-scale batch processing of requests.
     * 
     * @return the BatchService instance (lazily initialized)
     */
    public synchronized BatchService batches() {
        if (batchService == null) {
            this.batchService = new BatchServiceImpl(this);
        }
        return batchService;
    }

    /**
     * Returns the fine-tuning service for model customization.
     * This service handles training custom models on user data.
     * 
     * @return the FineTuningService instance (lazily initialized)
     */
    public synchronized FineTuningService fineTuning() {
        if (fineTuningService == null) {
            this.fineTuningService = new FineTuningServiceImpl(this);
        }
        return fineTuningService;
    }

    /**
     * Returns the web search service for internet search capabilities.
     * This service provides AI-powered web search functionality.
     * 
     * @return the WebSearchService instance (lazily initialized)
     */
    public synchronized WebSearchService webSearch() {
        if (webSearchService == null) {
            this.webSearchService = new WebSearchServiceImpl(this);
        }
        return webSearchService;
    }

    /**
     * Returns the videos service for video processing.
     * This service handles video analysis, generation, and manipulation.
     * 
     * @return the VideosService instance (lazily initialized)
     */
    public synchronized VideosService videos() {
        if (videosService == null) {
            this.videosService = new VideosServiceImpl(this);
        }
        return videosService;
    }

    /**
     * Returns the knowledge service for knowledge base operations.
     * This service manages knowledge bases and retrieval operations.
     * 
     * @return the KnowledgeService instance (lazily initialized)
     */
    public synchronized KnowledgeService knowledge() {
        if (knowledgeService == null) {
            this.knowledgeService = new KnowledgeServiceImpl(this);
        }
        return knowledgeService;
    }

    /**
     * Returns the document service for document processing.
     * This service handles document parsing, analysis, and manipulation.
     * 
     * @return the DocumentService instance (lazily initialized)
     */
    public synchronized DocumentService documents() {
        if (documentService == null) {
            this.documentService = new DocumentServiceImpl(this);
        }
        return documentService;
    }

    /**
     * Returns the assistant service for AI assistant functionality.
     * This service provides advanced AI assistant capabilities.
     * 
     * @return the AssistantService instance (lazily initialized)
     */
    public synchronized AssistantService assistants() {
        if (assistantService == null) {
            this.assistantService = new AssistantServiceImpl(this);
        }
        return assistantService;
    }

    // ==================== Utility Methods ====================

    /**
     * Returns the underlying Retrofit instance used for API communication.
     * This method is primarily intended for advanced users who need direct access
     * to the Retrofit client for custom API calls.
     * 
     * @return the Retrofit instance
     */
    public Retrofit retrofit() {
        return retrofit;
    }

    /**
     * Closes the ZAi client and releases all associated resources.
     * This method shuts down the HTTP client's connection pool and executor service.
     * After calling this method, the client should not be used for further requests.
     * 
     * <p><strong>Important:</strong> Always call this method when you're done with the client
     * to prevent resource leaks.</p>
     */
    public void close() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
        }
    }

    // ==================== Core Request Execution Methods ====================

    /**
     * Executes a synchronous API request and returns the response.
     * This method handles the complete request lifecycle including error handling
     * and response wrapping.
     * 
     * @param <Data> the type of data expected in the response
     * @param <Param> the type of parameters for the request
     * @param <TReq> the type of client request
     * @param <TResp> the type of client response
     * @param request the client request containing parameters
     * @param requestSupplier the supplier that creates the actual API call
     * @param tRespClass the class of the response type
     * @return the wrapped response containing either success data or error information
     */
    @Override
    public <Data, Param, TReq extends ClientRequest<Param>, TResp extends ClientResponse<Data>>
    TResp executeRequest(TReq request,
                         RequestSupplier<Param, Data> requestSupplier,
                         Class<TResp> tRespClass) {
        Single<Data> apiCall = requestSupplier.get((Param) request);

        TResp tResp = convertToClientResponse(tRespClass);
        try {
            // Execute the API call synchronously
            Data response = execute(apiCall);
            tResp.setCode(200);
            tResp.setMsg("Call successful");
            tResp.setData(response);
            tResp.setSuccess(true);
        } catch (ZAiHttpException e) {
            logger.error("API request failed with business error", e);
            tResp.setCode(e.statusCode);
            tResp.setMsg("Business error");
            tResp.setSuccess(false);
            ChatError chatError = new ChatError();
            chatError.setCode(Integer.parseInt(e.code));
            chatError.setMessage(e.getMessage());
            tResp.setError(chatError);
        }
        return tResp;
    }


    /**
     * Executes a streaming API request and returns a response containing a Flowable stream.
     * This method is used for requests that return data as a continuous stream,
     * such as chat completions with streaming enabled.
     * 
     * @param <Data> the type of data expected in each stream element
     * @param <Param> the type of parameters for the request
     * @param <TReq> the type of client request
     * @param <TResp> the type of flowable client response
     * @param request the client request containing parameters
     * @param requestSupplier the supplier that creates the actual streaming API call
     * @param tRespClass the class of the response type
     * @param tDataClass the class of the data type for stream elements
     * @return the wrapped response containing either a success stream or error information
     */
    @Override
    @SuppressWarnings("unchecked")
    public <Data, Param, TReq extends ClientRequest<Param>, TResp extends FlowableClientResponse<Data>>
    TResp streamRequest(TReq request,
                        FlowableRequestSupplier<Param, retrofit2.Call<ResponseBody>> requestSupplier,
                        Class<TResp> tRespClass,
                        Class<Data> tDataClass) {
        retrofit2.Call<ResponseBody> apiCall = requestSupplier.get((Param) request);

        TResp tResp = convertToClientResponse(tRespClass);
        try {
            // Create a streaming response using the provided API call
            Flowable<Data> stream = stream(apiCall, tDataClass);
            tResp.setCode(200);
            tResp.setMsg("Stream initialized successfully");
            tResp.setSuccess(true);
            tResp.setFlowable(stream);
        } catch (ZAiHttpException e) {
            logger.error("Streaming API request failed with business error", e);
            tResp.setCode(e.statusCode);
            tResp.setMsg("Business error");
            tResp.setSuccess(false);
            ChatError chatError = new ChatError();
            chatError.setCode(Integer.parseInt(e.code));
            chatError.setMessage(e.getMessage());
            tResp.setError(chatError);
        }
        return tResp;
    }

    /**
     * Creates a new instance of the specified response class using reflection.
     * This helper method is used to instantiate response objects dynamically.
     * 
     * @param <Data> the type of data contained in the response
     * @param <TResp> the type of client response
     * @param tRespClass the class of the response type to instantiate
     * @return a new instance of the response class
     * @throws RuntimeException if the response object cannot be created
     */
    private <Data, TResp extends ClientResponse<Data>> TResp convertToClientResponse(Class<TResp> tRespClass) {
        try {
            return tRespClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException("Failed to create response object of type: " + tRespClass.getSimpleName(), e);
        }
    }

    // ==================== Builder Pattern Implementation ====================

    /**
     * Builder class for creating ZAiClient instances with custom configurations.
     * This builder provides a fluent API for setting up the client with various
     * options including authentication, network settings, and connection pooling.
     * 
     * <p>Example usage:</p>
     * <pre>{@code
     * ZAiClient client = new ZAiClient.Builder("your-api-key")
     *     .networkConfig(30, 10, 30, 30, TimeUnit.SECONDS)
     *     .connectionPool(10, 5, TimeUnit.MINUTES)
     *     .enableTokenCache()
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /** Configuration object that accumulates all builder settings */
        private final ZAiConfig config = new ZAiConfig();

        public Builder() {}

        /**
         * Creates a new builder with the specified API key.
         * 
         * @param apiKey the API key for authentication
         * @throws IllegalArgumentException if apiKey is null or empty
         */
        public Builder(String apiKey) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API key cannot be null or empty");
            }
            config.setApiKey(apiKey);
        }

        /**
         * Creates a new builder with a custom base URL and API secret key.
         * 
         * @param baseUrl the custom base URL for the API endpoint
         * @param apiKey the API secret key for authentication
         * @throws IllegalArgumentException if any parameter is null or empty
         */
        public Builder(String baseUrl, String apiKey) {
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("Base URL cannot be null or empty");
            }
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API secret key cannot be null or empty");
            }
            config.setBaseUrl(baseUrl);
            config.setApiKey(apiKey);
        }

        /**
         * Config the api service base url
         * @param baseUrl base url
         *
         * @return this Builder instance for method chaining
         */
        public Builder baseUrl(String baseUrl) {
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("Base URL cannot be null or empty");
            }
            config.setBaseUrl(baseUrl);
            return this;
        }

        /**
         * Config the apikey
         * @param apiKey api key
         * @return this Builder instance for method chaining
         */
        public Builder apiKey(String apiKey) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API secret key cannot be null or empty");
            }
            config.setApiKey(apiKey);
            return this;
        }

        /**
         * Use the ZHIPU AI base url
         * @return this Builder instance for method chaining
         */
        public Builder ofZHIPU() {
            config.setBaseUrl(ZHIPU_AI_BASE_URL);
            return this;
        }

        /**
         * Use the Z AI base url
         * @return this Builder instance for method chaining
         */
        public Builder ofZAI() {
            config.setBaseUrl(Z_AI_BASE_URL);
            return this;
        }

        /**
         * Disables token caching, forcing the client to use API keys for direct requests.
         * This is the default behavior.
         * 
         * @return this Builder instance for method chaining
         */
        public Builder disableTokenCache() {
            config.setDisableTokenCache(true);
            return this;
        }

        /**
         * Enables token caching, allowing the client to use access tokens for requests.
         * This can improve performance by reducing authentication overhead.
         * 
         * @return this Builder instance for method chaining
         */
        public Builder enableTokenCache() {
            config.setDisableTokenCache(false);
            return this;
        }

        /**
         * Configures the HTTP connection pool settings.
         * 
         * @param maxIdleConnections maximum number of idle connections to keep in the pool
         * @param keepAliveDuration how long to keep idle connections alive
         * @param timeUnit the time unit for the keep alive duration
         * @return this Builder instance for method chaining
         */
        public Builder connectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
            config.setConnectionPoolMaxIdleConnections(maxIdleConnections);
            config.setConnectionPoolKeepAliveDuration(keepAliveDuration);
            config.setConnectionPoolTimeUnit(timeUnit);
            return this;
        }

        /**
         * Sets the token expiration time in milliseconds.
         * 
         * @param expireMillis the token expiration time in milliseconds
         * @return this Builder instance for method chaining
         */
        public Builder tokenExpire(int expireMillis) {
            config.setExpireMillis(expireMillis);
            return this;
        }

        /**
         * Configures network request timeout settings.
         * 
         * @param requestTimeOut the overall request timeout (see {@link OkHttpClient.Builder#callTimeout(long, TimeUnit)})
         * @param connectTimeout the connection timeout (see {@link OkHttpClient.Builder#connectTimeout(long, TimeUnit)})
         * @param readTimeout the read timeout (see {@link OkHttpClient.Builder#readTimeout(long, TimeUnit)})
         * @param writeTimeout the write timeout (see {@link OkHttpClient.Builder#writeTimeout(long, TimeUnit)})
         * @param timeUnit the time unit for all timeout values
         * @return this Builder instance for method chaining
         */
        public Builder networkConfig(int requestTimeOut,
                                     int connectTimeout,
                                     int readTimeout,
                                     int writeTimeout,
                                     TimeUnit timeUnit) {
            config.setRequestTimeOut(requestTimeOut);
            config.setConnectTimeout(connectTimeout);
            config.setReadTimeout(readTimeout);
            config.setWriteTimeout(writeTimeout);
            config.setTimeOutTimeUnit(timeUnit);
            return this;
        }

        /**
         * Builds and returns a new ZAiClient instance with the configured settings.
         * 
         * @return a new ZAiClient instance
         * @throws IllegalStateException if the configuration is invalid
         */
        public ZAiClient build() {
            return new ZAiClient(config);
        }
    }

    // ==================== Static Factory Methods ====================

    /**
     * Creates a new Builder instance for constructing ZAiClient.
     * 
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a ZAiClient configured for ZHIPU AI platform with the specified API key.
     * 
     * @param apiKey the API key for authentication
     * @return a new ZAiClient instance configured for ZHIPU AI
     * @throws IllegalArgumentException if apiKey is null or empty
     */
    public static Builder ofZHIPU(String apiKey) {
        return new Builder()
                .apiKey(apiKey)
                .ofZHIPU();
    }

    /**
     * Creates a ZAiClient configured for ZHIPU AI platform with the specified API key.
     *
     * @return a new ZAiClient instance configured for ZHIPU AI
     * @throws IllegalArgumentException if apiKey is null or empty
     */
    public static Builder ofZHIPU() {
        return new Builder()
                .ofZHIPU();
    }

}
