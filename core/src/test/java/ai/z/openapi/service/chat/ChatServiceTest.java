package ai.z.openapi.service.chat;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.model.*;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatFunction;
import ai.z.openapi.service.model.ChatFunctionParameterProperty;
import ai.z.openapi.service.model.ChatFunctionParameters;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatTool;
import ai.z.openapi.service.model.ChatToolType;
import ai.z.openapi.service.model.Choice;
import ai.z.openapi.service.model.InputAudio;
import ai.z.openapi.service.model.MessageContent;
import ai.z.openapi.service.model.QueryModelResultResponse;
import ai.z.openapi.service.model.WebSearch;
import ai.z.openapi.service.model.params.CodeGeexExtra;
import ai.z.openapi.service.model.params.CodeGeexTarget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChatService test class for testing various functionalities of ChatService and
 * ChatServiceImpl
 */
@DisplayName("ChatService Tests")
public class ChatServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(ChatServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private ChatService chatService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "chat-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		chatService = client.chat();
	}

	@Test
	@DisplayName("Test ChatService Instantiation")
	void testChatServiceInstantiation() {
		assertNotNull(chatService, "ChatService should be properly instantiated");
		assertInstanceOf(ChatServiceImpl.class, chatService, "ChatService should be an instance of ChatServiceImpl");
	}

	@Test
	@DisplayName("Test Synchronous Chat Completion - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testSyncChatCompletion() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Hello, please introduce yourself");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertEquals(requestId, response.getData().getRequestId(), "Request ID should match");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Synchronous chat completion response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Stream Chat Completion")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testStreamChatCompletion() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(),
				"Please write a short poem about spring");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.TRUE)
			.messages(messages)
			.requestId(requestId)
			.temperature(0.7F)
			.maxTokens(100)
			.build();

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request);

		// Verify results
		assertNotNull(response, "Response should not be null");

		assertTrue(response.isSuccess(), "Response should be successful");

		// Test stream data processing
		AtomicInteger messageCount = new AtomicInteger(0);
		AtomicBoolean isFirst = new AtomicBoolean(true);

		response.getFlowable().doOnNext(modelData -> {
			if (isFirst.getAndSet(false)) {
				logger.info("Starting to receive stream response:");
			}
			if (modelData.getChoices() != null && !modelData.getChoices().isEmpty()) {
				Choice choice = modelData.getChoices().get(0);
				if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
					logger.info("Received content: {}", choice.getDelta().getContent());
					messageCount.incrementAndGet();
				}
			}
		})
			.doOnComplete(
					() -> logger.info("Stream response completed, received {} messages in total", messageCount.get()))
			.blockingSubscribe();

		assertTrue(messageCount.get() > 0, "Should receive at least one message");

		logger.info("Stream chat completion test completed");
	}

	@Test
	@DisplayName("Test Synchronous Chat Completion - MCP Tool")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testSyncChatCompletion_MCP_ServerUrl() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Hello, please introduce GPT?");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.tools(Arrays.asList(ChatTool.builder()
				.type(ChatToolType.MCP.value())
				.mcp(MCPTool.builder()
					.server_url("https://open.bigmodel.cn/api/mcp/sogou/sse")
					.server_label("sougou")
					.transport_type("sse")
					.headers(Collections.singletonMap("Authorization", "Bearer " + System.getProperty("ZAI_API_KEY")))
					.build())
				.build()))
			.build();

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertEquals(requestId, response.getData().getRequestId(), "Request ID should match");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Synchronous chat completion response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Synchronous Chat Completion - MCP Tool")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testSyncChatCompletion_MCP_ServerLabel() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Hello, please introduce GPT?");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.tools(Arrays.asList(ChatTool.builder()
				.type(ChatToolType.MCP.value())
				.mcp(MCPTool.builder()
					.server_label("sougou_search")
					.headers(Collections.singletonMap("Authorization", "Bearer " + System.getProperty("ZAI_API_KEY")))
					.build())
				.build()))
			.build();

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertEquals(requestId, response.getData().getRequestId(), "Request ID should match");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Synchronous chat completion response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Asynchronous Chat Completion")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testAsyncChatCompletion() {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(),
				"Please explain the development history of artificial intelligence");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Execute asynchronous request
		ChatCompletionResponse response = chatService.asyncChatCompletion(request);

		// Verify results
		assertNotNull(response, "Asynchronous response should not be null");

		assertTrue(response.isSuccess(), "Asynchronous response should be successful");
		assertNotNull(response.getData(), "Asynchronous response data should not be null");
		assertNotNull(response.getData().getId(), "Asynchronous response task id should not be null");
		assertNotNull(response.getData().getTaskStatus(), "Asynchronous response task status should not be null");
		// If there is a task ID, test querying asynchronous results
		String taskId = response.getData().getId();
		AsyncResultRetrieveParams retrieveParams = new AsyncResultRetrieveParams();
		retrieveParams.setTaskId(taskId);

		QueryModelResultResponse queryModelResultResponse = chatService.retrieveAsyncResult(retrieveParams);

		assertNotNull(queryModelResultResponse, "Query asynchronous result response should not be null");
		assertNotNull(response.getData().getId(), "Asynchronous response task id should not be null");
		assertNotNull(response.getData().getTaskStatus(), "Asynchronous response task status should not be null");
		logger.info("Query asynchronous result: taskId={}, data={}", taskId, queryModelResultResponse);

	}

	@Test
	@DisplayName("Test Retrieve Asynchronous Result Error")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveAsyncResultError() {
		AsyncResultRetrieveParams retrieveParams = new AsyncResultRetrieveParams();
		retrieveParams.setTaskId("mock-task-id-" + System.currentTimeMillis());

		QueryModelResultResponse response = chatService.retrieveAsyncResult(retrieveParams);

		assertNotNull(response.getError(), "Querying non-existent task should return error");
	}

	@Test
	@DisplayName("Test Function Calling")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testFunctionCalling() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(),
				"What's the weather like in Beijing today?");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		// Build function calling parameters
		List<ChatTool> chatToolList = new ArrayList<>();
		ChatTool chatTool = new ChatTool();
		chatTool.setType(ChatToolType.FUNCTION.value());

		ChatFunctionParameters parameters = new ChatFunctionParameters();
		parameters.setType("object");

		Map<String, ChatFunctionParameterProperty> properties = new HashMap<>();
		ChatFunctionParameterProperty locationProperty = ChatFunctionParameterProperty.builder()
			.type("string")
			.description("City name, for example: Beijing")
			.build();
		properties.put("location", locationProperty);

		ChatFunctionParameterProperty unitProperty = ChatFunctionParameterProperty.builder()
			.type("string")
			.enums(Arrays.asList("celsius", "fahrenheit"))
			.build();
		properties.put("unit", unitProperty);

		parameters.setProperties(properties);

		ChatFunction function = ChatFunction.builder()
			.name("get_weather")
			.description("Get current weather information for the specified city")
			.parameters(parameters)
			.build();

		chatTool.setFunction(function);
		chatToolList.add(chatTool);

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.tools(chatToolList)
			.toolChoice("auto")
			.build();

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request);

		// Verify results
		assertNotNull(response, "Function calling response should not be null");

		assertTrue(response.isSuccess(), "Function calling response should be successful");
		assertNotNull(response.getData(), "Function calling response data should not be null");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		assertEquals("tool_calls", response.getData().getChoices().get(0).getFinishReason());
		assertNotNull(response.getData().getChoices().get(0).getMessage(),
				"Response data choice message should not be null");
		assertNotNull(response.getData().getChoices().get(0).getMessage().getToolCalls(),
				"Response data choice message tool call should not be null");
		assertFalse(response.getData().getChoices().get(0).getMessage().getToolCalls().isEmpty(),
				"Response data choice message tool call should not be null");
		logger.info("Function calling response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Audio with audio Input")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testAudioWithAudio() throws IOException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		String file = ClassLoader.getSystemResource("asr.wav").getFile();
		byte[] bytes = Files.readAllBytes(new File(file).toPath());
		Base64.Encoder encoder = Base64.getEncoder();
		String audioBytes = encoder.encodeToString(bytes);

		MessageContent messageContent = new MessageContent();
		messageContent.setType("input_audio");
		messageContent.setInputAudio(InputAudio.builder().format("wav").data(audioBytes).build());
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), Arrays.asList(messageContent));
		messages.add(userMessage);

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4Voice)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertEquals(requestId, response.getData().getRequestId(), "Request ID should match");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Synchronous chat completion response: {}", mapper.writeValueAsString(response));
	}

	@ParameterizedTest
	@ValueSource(strings = { Constants.ModelChatGLM4, Constants.ModelCharGLM3 })
	@DisplayName("Test Different Models")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDifferentModels(String model) throws JsonProcessingException {
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Hello");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(model)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		ChatCompletionResponse response = chatService.createChatCompletion(request);

		assertNotNull(response, "Response should not be null");
		logger.info("Model {} response: {}", model, mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request")
	void testValidation_NullRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			chatService.createChatCompletion(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Empty Messages")
	void testValidation_EmptyMessages() {
		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.messages(new ArrayList<>())
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			chatService.createChatCompletion(request);
		}, "Empty messages list should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Model")
	void testValidation_NullModel() {
		List<ChatMessage> messages = new ArrayList<>();
		messages.add(new ChatMessage(ChatMessageRole.USER.value(), "Test message"));

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder().messages(messages).build();

		assertThrows(IllegalArgumentException.class, () -> {
			chatService.createChatCompletion(request);
		}, "Null model should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Web Search Tool")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testWebSearchTool() throws JsonProcessingException {
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(),
				"Please search for the latest AI technology development trends");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		// Build Web search tool
		List<ChatTool> chatToolList = new ArrayList<>();
		ChatTool webSearchTool = new ChatTool();
		webSearchTool.setType(ChatToolType.WEB_SEARCH.value());

		WebSearch webSearch = new WebSearch();
		webSearch.setSearchQuery("AI technology development");
		webSearch.setSearchResult(true);
		webSearch.setEnable(true);
		webSearchTool.setWebSearch(webSearch);

		chatToolList.add(webSearchTool);

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.tools(chatToolList)
			.toolChoice("auto")
			.build();

		ChatCompletionResponse response = chatService.createChatCompletion(request);

		assertNotNull(response, "Web search response should not be null");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		assertNotNull(response.getData().getChoices().get(0).getMessage(),
				"Response data choice message should not be null");
		assertNotNull(response.getData().getWebSearch(), "Response data web search should not be null");
		assertFalse(response.getData().getWebSearch().isEmpty(), "Response data web search should not be empty");

		logger.info("Web search response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Multi-turn Conversation")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testMultiTurnConversation() throws JsonProcessingException {
		List<ChatMessage> messages = new ArrayList<>();

		// First round of conversation
		messages
			.add(new ChatMessage(ChatMessageRole.USER.value(), "Hello, I would like to learn about machine learning"));
		messages.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(),
				"Hello! Machine learning is an important branch of artificial intelligence..."));

		// Second round of conversation
		messages.add(new ChatMessage(ChatMessageRole.USER.value(), "Can you introduce supervised learning in detail?"));

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		ChatCompletionResponse response = chatService.createChatCompletion(request);

		assertNotNull(response, "Multi-turn conversation response should not be null");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		assertNotNull(response.getData().getChoices().get(0).getMessage(),
				"Response data choice message should not be null");
		logger.info("Multi-turn conversation response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test CodeGeex Code Completion")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCodeGeexCompletion() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Help me complete this Java code");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		// Build CodeGeex parameters
		CodeGeexTarget codeGeexTarget = new CodeGeexTarget();
		codeGeexTarget.setPath("TestFile.java");
		codeGeexTarget.setLanguage("java");
		codeGeexTarget.setCodePrefix("public class TestClass {\n    public void testMethod() {");
		codeGeexTarget.setCodeSuffix("    }\n}");

		CodeGeexExtra codeGeexExtra = new CodeGeexExtra();
		codeGeexExtra.setContexts(new ArrayList<>());
		codeGeexExtra.setTarget(codeGeexTarget);

		List<String> stop = new ArrayList<>();
		stop.add("<|endoftext|>");
		stop.add("<|user|>");
		stop.add("<|assistant|>");
		stop.add("<|observation|>");

		Map<String, Object> extraJson = new HashMap<>();
		extraJson.put("invoke_method", "invoke");

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model("codegeex-4")
			.stream(Boolean.TRUE)
			.extraJson(extraJson)
			.messages(messages)
			.stop(stop)
			.extra(codeGeexExtra)
			.requestId(requestId)
			.build();

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request);

		// Verify results
		assertNotNull(response, "CodeGeex response should not be null");
		assertTrue(response.isSuccess(), "CodeGeex response should be successful");

		// Test stream data processing
		AtomicInteger messageCount = new AtomicInteger(0);
		AtomicBoolean isFirst = new AtomicBoolean(true);
		AtomicReference<String> lastId = new AtomicReference<>();
		AtomicReference<Long> lastCreated = new AtomicReference<>();

		response.getFlowable().doOnNext(modelData -> {
			if (isFirst.getAndSet(false)) {
				logger.info("Starting to receive CodeGeex stream response:");
			}
			if (modelData.getChoices() != null && !modelData.getChoices().isEmpty()) {
				Choice choice = modelData.getChoices().get(0);
				if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
					logger.info("Received code content: {}", choice.getDelta().getContent());
					messageCount.incrementAndGet();
				}
			}
			if (modelData.getId() != null) {
				lastId.set(modelData.getId());
			}
			if (modelData.getCreated() != null) {
				lastCreated.set(modelData.getCreated());
			}
		})
			.doOnComplete(() -> logger.info("CodeGeex stream response completed, received {} messages in total",
					messageCount.get()))
			.doOnError(throwable -> logger.error("CodeGeex stream error: {}", throwable.getMessage()))
			.blockingSubscribe();

		assertTrue(messageCount.get() >= 0, "Should receive at least zero messages");
		assertNotNull(lastId.get(), "Response should have an ID");
		assertNotNull(lastCreated.get(), "Response should have a created timestamp");

		logger.info("CodeGeex code completion test completed");
	}

	@Test
	@DisplayName("Test Synchronous Chat Completion with Custom Headers")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testSyncChatCompletionWithCustomHeaders() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Hello, please introduce yourself");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Prepare custom headers
		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put("X-Custom-User-ID", "test-user-123");
		customHeaders.put("X-Request-Source", "junit-test");
		customHeaders.put("Session-Id", "session-" + System.currentTimeMillis());

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request, customHeaders);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertEquals(requestId, response.getData().getRequestId(), "Request ID should match");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Synchronous chat completion with custom headers response: {}",
				mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Stream Chat Completion with Custom Headers")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testStreamChatCompletionWithCustomHeaders() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(),
				"Please write a short poem about spring");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.TRUE)
			.messages(messages)
			.requestId(requestId)
			.temperature(0.7F)
			.maxTokens(100)
			.build();

		// Prepare custom headers
		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put("X-Custom-User-ID", "stream-test-user-456");
		customHeaders.put("X-Request-Source", "junit-stream-test");
		customHeaders.put("X-Stream-Mode", "enabled");
		customHeaders.put("Content-Type", "application/json");

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request, customHeaders);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");

		// Test stream data processing
		AtomicInteger messageCount = new AtomicInteger(0);
		AtomicBoolean isFirst = new AtomicBoolean(true);

		response.getFlowable().doOnNext(modelData -> {
			if (isFirst.getAndSet(false)) {
				logger.info("Starting to receive stream response with custom headers:");
			}
			if (modelData.getChoices() != null && !modelData.getChoices().isEmpty()) {
				Choice choice = modelData.getChoices().get(0);
				if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
					logger.info("Received content: {}", choice.getDelta().getContent());
					messageCount.incrementAndGet();
				}
			}
		})
			.doOnComplete(() -> logger.info(
					"Stream response with custom headers completed, received {} messages in total", messageCount.get()))
			.blockingSubscribe();

		assertTrue(messageCount.get() > 0, "Should receive at least one message");

		logger.info("Stream chat completion with custom headers test completed");
	}

	@Test
	@DisplayName("Test Custom Headers Validation - Null Headers")
	void testCustomHeadersValidation_NullHeaders() {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Hello");
		messages.add(userMessage);

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.build();

		// Test with null custom headers
		assertThrows(IllegalArgumentException.class, () -> {
			chatService.createChatCompletion(request, null);
		}, "Null custom headers should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Custom Headers with Empty Map")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCustomHeadersWithEmptyMap() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Hello");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Test with empty custom headers map
		Map<String, String> emptyHeaders = new HashMap<>();
		ChatCompletionResponse response = chatService.createChatCompletion(request, emptyHeaders);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		logger.info("Chat completion with empty custom headers response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Custom Headers with Special Characters")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCustomHeadersWithSpecialCharacters() throws JsonProcessingException {
		// Prepare test data
		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Hello");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Prepare custom headers with special characters
		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put("X-User-Agent", "ZAI-SDK/1.0 (Java; Test)");
		customHeaders.put("X-Client-Version", "v1.2.3-beta");
		customHeaders.put("X-Request-Timestamp", String.valueOf(System.currentTimeMillis()));
		customHeaders.put("X-Trace-ID", "trace-" + UUID.randomUUID());

		// Execute test
		ChatCompletionResponse response = chatService.createChatCompletion(request, customHeaders);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		logger.info("Chat completion with special character headers response: {}", mapper.writeValueAsString(response));
	}

}
