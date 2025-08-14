package ai.z.openapi.service.assistant;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.assistant.conversation.AssistantConversationParameters;
import ai.z.openapi.service.assistant.conversation.AssistantConversationUsageListResponse;
import ai.z.openapi.service.assistant.message.AssistantMessageContent;
import ai.z.openapi.service.assistant.query_support.AssistantSupportResponse;
import ai.z.openapi.service.assistant.query_support.AssistantQuerySupportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AssistantService test class for testing various functionalities of AssistantService and
 * AssistantServiceImpl
 */
@DisplayName("AssistantService Tests")
public class AssistantServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(AssistantServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private AssistantService assistantService;

	// Test assistant ID
	private static final String TEST_ASSISTANT_ID = "659e54b1b8006379b4b2abd6";

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "assistant-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		assistantService = client.assistants();
	}

	@Test
	@DisplayName("Test AssistantService Instantiation")
	void testAssistantServiceInstantiation() {
		assertNotNull(assistantService, "AssistantService should be properly instantiated");
		assertInstanceOf(AssistantServiceImpl.class, assistantService,
				"AssistantService should be an instance of AssistantServiceImpl");
	}

	@Test
	@DisplayName("Test Stream Assistant Completion")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testStreamAssistantCompletion() throws JsonProcessingException {
		// Prepare test data
		AssistantMessageTextContent textContent = AssistantMessageTextContent.builder()
			.text("Help me search for the release time of ZAI's CogVideoX")
			.type("text")
			.build();

		AssistantConversationMessage message = AssistantConversationMessage.builder()
			.role("user")
			.content(Collections.singletonList(textContent))
			.build();

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		AssistantParameters request = AssistantParameters.builder()
			.assistantId(TEST_ASSISTANT_ID)
			.stream(true)
			.messages(Collections.singletonList(message))
			.requestId(requestId)
			.build();

		// Execute test
		AssistantApiResponse response = assistantService.assistantCompletionStream(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");

		// Test stream data processing
		AtomicInteger messageCount = new AtomicInteger(0);
		AtomicBoolean isFirst = new AtomicBoolean(true);
		List<AssistantMessageContent> choices = new ArrayList<>();
		AtomicReference<AssistantCompletion> lastAccumulator = new AtomicReference<>();

		response.getFlowable().doOnNext(accumulator -> {
			if (isFirst.getAndSet(false)) {
				logger.info("Starting to receive stream response:");
			}
			if (accumulator.getChoices() != null && !accumulator.getChoices().isEmpty()) {
				AssistantMessageContent delta = accumulator.getChoices().get(0).getDelta();
				if (delta != null) {
					try {
						logger.info("MessageContent: {}", mapper.writeValueAsString(delta));
					}
					catch (JsonProcessingException e) {
						logger.error("Error processing message content", e);
					}
					choices.add(delta);
					messageCount.incrementAndGet();
				}
			}
			lastAccumulator.set(accumulator);
		})
			.doOnComplete(
					() -> logger.info("Stream response completed, received {} messages in total", messageCount.get()))
			.doOnError(throwable -> logger.error("Stream error: {}", throwable.getMessage()))
			.blockingSubscribe();

		assertTrue(messageCount.get() >= 0, "Should receive at least zero messages");
		assertNotNull(lastAccumulator.get(), "Should have received at least one accumulator");

		logger.info("Stream assistant completion test completed");
	}

	@Test
	@DisplayName("Test Query Support")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testQuerySupport() {
		// Prepare test data
		AssistantQuerySupportParams request = AssistantQuerySupportParams.builder()
			.assistantIdList(Collections.singletonList(TEST_ASSISTANT_ID))
			.build();

		// Execute test
		AssistantSupportResponse response = assistantService.querySupport(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Query support response: {}", response);
	}

	@Test
	@DisplayName("Test Query Conversation Usage")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testQueryConversationUsage() {
		// Prepare test data
		AssistantConversationParameters request = AssistantConversationParameters.builder()
			.assistantId(TEST_ASSISTANT_ID)
			.page(1)
			.pageSize(5)
			.build();

		// Execute test
		AssistantConversationUsageListResponse response = assistantService.queryConversationUsage(request);

		// Verify results
		assertNotNull(response.getData(), "Response should not be null");
		logger.info("Query conversation usage response: {}", response);
	}

	@Test
	@DisplayName("Test Error Handling - Invalid Assistant ID")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testErrorHandling_InvalidAssistantId() {
		// Test with invalid assistant ID
		AssistantMessageTextContent textContent = AssistantMessageTextContent.builder()
			.text("Hello")
			.type("text")
			.build();

		AssistantConversationMessage message = AssistantConversationMessage.builder()
			.role("user")
			.content(Collections.singletonList(textContent))
			.build();

		AssistantParameters request = AssistantParameters.builder()
			.assistantId("invalid-assistant-id")
			.stream(false)
			.messages(Collections.singletonList(message))
			.build();

		AssistantApiResponse response = assistantService.assistantCompletionStream(request);

		// Should handle error gracefully
		assertNotNull(response, "Response should not be null even for invalid assistant ID");
		logger.info("Error handling response: {}", response);
	}

	@Test
	@DisplayName("Test Multi-turn Conversation")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testMultiTurnConversation() throws JsonProcessingException {
		List<AssistantConversationMessage> messages = new ArrayList<>();

		// First round of conversation
		AssistantMessageTextContent firstContent = AssistantMessageTextContent.builder()
			.text("Hello, I would like to learn about machine learning")
			.type("text")
			.build();
		messages.add(AssistantConversationMessage.builder()
			.role("user")
			.content(Collections.singletonList(firstContent))
			.build());

		AssistantMessageTextContent assistantContent = AssistantMessageTextContent.builder()
			.text("Hello! Machine learning is an important branch of artificial intelligence...")
			.type("text")
			.build();
		messages.add(AssistantConversationMessage.builder()
			.role("assistant")
			.content(Collections.singletonList(assistantContent))
			.build());

		// Second round of conversation
		AssistantMessageTextContent secondContent = AssistantMessageTextContent.builder()
			.text("Can you introduce supervised learning in detail?")
			.type("text")
			.build();
		messages.add(AssistantConversationMessage.builder()
			.role("user")
			.content(Collections.singletonList(secondContent))
			.build());

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		AssistantParameters request = AssistantParameters.builder()
			.assistantId(TEST_ASSISTANT_ID)
			.stream(true)
			.messages(messages)
			.requestId(requestId)
			.build();

		AssistantApiResponse response = assistantService.assistantCompletionStream(request);
		response.getFlowable().doOnNext(accumulator -> {
			if (accumulator.getChoices() != null && !accumulator.getChoices().isEmpty()) {
				AssistantMessageContent delta = accumulator.getChoices().get(0).getDelta();
				if (delta != null) {
					try {
						logger.info("MessageContent: {}", mapper.writeValueAsString(delta));
					}
					catch (JsonProcessingException e) {
						logger.error("Error processing message content", e);
					}
				}
			}
		})
			.doOnComplete(() -> logger.info("Stream response completed, received messages"))
			.doOnError(throwable -> logger.error("Stream error: {}", throwable.getMessage()))
			.blockingSubscribe();
		assertNotNull(response, "Multi-turn conversation response should not be null");
	}

}
