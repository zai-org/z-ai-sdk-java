package ai.z.openapi.service.agents;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.Choice;
import ai.z.openapi.service.model.ModelData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AgentService test class for testing various functionalities of AgentService and
 * AgentServiceImpl
 */
@DisplayName("AgentService Tests")
@Disabled
public class AgentServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(AgentServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private AgentService agentService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "agent-test-%d";

	// Test agent ID
	private static final String TEST_AGENT_ID = "general_translation";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		agentService = client.agents();
	}

	@Test
	@DisplayName("Test AgentService Instantiation")
	void testAgentServiceInstantiation() {
		assertNotNull(agentService, "AgentService should be properly instantiated");
		assertInstanceOf(AgentServiceImpl.class, agentService,
				"AgentService should be an instance of AgentServiceImpl");
	}

	@Test
	@DisplayName("Test Synchronous Agent Completion - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testSyncAgentCompletion() throws JsonProcessingException {
		// Prepare test data
		List<AgentMessage> messages = new ArrayList<>();
		AgentMessage userMessage = new AgentMessage(ChatMessageRole.USER.value(),
				AgentContent.ofText("Hello, please translate this to Chinese: How are you?"));
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		AgentsCompletionRequest request = AgentsCompletionRequest.builder()
			.agentId(TEST_AGENT_ID)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Execute test
		ChatCompletionResponse response = agentService.createAgentCompletion(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertEquals(requestId, response.getData().getRequestId(), "Request ID should match");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Synchronous agent completion response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Stream Agent Completion")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testStreamAgentCompletion() throws JsonProcessingException {
		// Prepare test data
		List<AgentMessage> messages = new ArrayList<>();
		AgentMessage userMessage = new AgentMessage(ChatMessageRole.USER.value(),
				AgentContent.ofText("Please translate this to Chinese: The weather is beautiful today"));
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		AgentsCompletionRequest request = AgentsCompletionRequest.builder()
			.agentId(TEST_AGENT_ID)
			.stream(Boolean.TRUE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Execute test
		ChatCompletionResponse response = agentService.createAgentCompletion(request);

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

		logger.info("Stream agent completion test completed");
	}

	@Test
	@DisplayName("Test Retrieve Agent Async Result")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveAgentAsyncResult() {
		// Prepare test data
		String taskId = "test-task-id-" + System.currentTimeMillis();
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		AgentAsyncResultRetrieveParams retrieveParams = AgentAsyncResultRetrieveParams.builder()
			.taskId(taskId)
			.agentId(TEST_AGENT_ID)
			.requestId(requestId)
			.build();

		// Execute test
		Single<ModelData> response = agentService.retrieveAgentAsyncResult(retrieveParams);

		// Verify results
		assertNotNull(response, "Response should not be null");

		// Note: Since this is a test with a mock task ID, we expect it to handle the
		// error gracefully
		try {
			ModelData result = response.blockingGet();
			logger.info("Retrieve agent async result: {}", result);
		}
		catch (Exception e) {
			// Expected for non-existent task ID
			logger.info("Expected error for non-existent task ID: {}", e.getMessage());
			assertTrue(
					e.getMessage().contains("task") || e.getMessage().contains("not found")
							|| e.getMessage().contains("error"),
					"Error message should indicate task not found or similar");
		}
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request")
	void testValidation_NullRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			agentService.createAgentCompletion(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Empty Messages")
	void testValidation_EmptyMessages() {
		AgentsCompletionRequest request = AgentsCompletionRequest.builder()
			.agentId(TEST_AGENT_ID)
			.messages(new ArrayList<>())
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			agentService.createAgentCompletion(request);
		}, "Empty messages list should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Agent ID")
	void testValidation_NullAgentId() {
		List<AgentMessage> messages = new ArrayList<>();
		messages.add(new AgentMessage(ChatMessageRole.USER.value(), "Test message"));

		AgentsCompletionRequest request = AgentsCompletionRequest.builder().messages(messages).build();

		assertThrows(IllegalArgumentException.class, () -> {
			agentService.createAgentCompletion(request);
		}, "Null agent ID should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Messages")
	void testValidation_NullMessages() {
		AgentsCompletionRequest request = AgentsCompletionRequest.builder().agentId(TEST_AGENT_ID).build();

		assertThrows(IllegalArgumentException.class, () -> {
			agentService.createAgentCompletion(request);
		}, "Null messages should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Multi-turn Conversation with Agent")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testMultiTurnConversationWithAgent() throws JsonProcessingException {
		List<AgentMessage> messages = new ArrayList<>();

		// First round of conversation
		messages.add(new AgentMessage(ChatMessageRole.USER.value(),
				AgentContent.ofText("Please translate 'Hello' to Chinese")));
		messages.add(new AgentMessage(ChatMessageRole.ASSISTANT.value(), AgentContent.ofText("你好")));

		// Second round of conversation
		messages.add(new AgentMessage(ChatMessageRole.USER.value(),
				AgentContent.ofText("Now translate 'Thank you' to Chinese")));

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		AgentsCompletionRequest request = AgentsCompletionRequest.builder()
			.agentId(TEST_AGENT_ID)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		ChatCompletionResponse response = agentService.createAgentCompletion(request);

		assertNotNull(response, "Multi-turn conversation response should not be null");
		assertNotNull(response.getData().getChoices(), "Response data should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		assertNotNull(response.getData().getChoices().get(0).getMessage(),
				"Response data choice message should not be null");
		logger.info("Multi-turn conversation with agent response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Agent Completion with Custom Variables")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testAgentCompletionWithCustomVariables() throws JsonProcessingException {
		List<AgentMessage> messages = new ArrayList<>();
		AgentMessage userMessage = new AgentMessage(ChatMessageRole.USER.value(),
				AgentContent.ofText("Translate this text"));
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		// Note: custom_variables would typically be set if the agent supports it
		AgentsCompletionRequest request = AgentsCompletionRequest.builder()
			.agentId(TEST_AGENT_ID)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		ChatCompletionResponse response = agentService.createAgentCompletion(request);

		assertNotNull(response, "Response with custom variables should not be null");
		logger.info("Agent completion with custom variables response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Retrieve Async Result Error")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveAsyncResultError() {
		AgentAsyncResultRetrieveParams retrieveParams = AgentAsyncResultRetrieveParams.builder()
			.taskId("mock-task-id-" + System.currentTimeMillis())
			.agentId("non-existent-agent")
			.build();

		Single<ModelData> response = agentService.retrieveAgentAsyncResult(retrieveParams);

		assertNotNull(response, "Response should not be null even for non-existent task");

		// Expect an error when trying to retrieve non-existent task
		assertThrows(Exception.class, response::blockingGet, "Querying non-existent task should throw an exception");
	}

}
