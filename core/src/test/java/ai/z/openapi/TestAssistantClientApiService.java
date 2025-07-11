package ai.z.openapi;

import ai.z.openapi.core.config.ZAiConfig;
import ai.z.openapi.service.assistant.AssistantApiResponse;
import ai.z.openapi.service.assistant.AssistantCompletion;
import ai.z.openapi.service.assistant.AssistantExtraParameters;
import ai.z.openapi.service.assistant.AssistantParameters;
import ai.z.openapi.service.assistant.ConversationMessage;
import ai.z.openapi.service.assistant.MessageTextContent;
import ai.z.openapi.service.assistant.TranslateParameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ai.z.openapi.service.assistant.conversation.ConversationParameters;
import ai.z.openapi.service.assistant.conversation.ConversationUsageListResponse;
import ai.z.openapi.service.assistant.message.MessageContent;
import ai.z.openapi.service.assistant.query_support.AssistantSupportResponse;
import ai.z.openapi.service.assistant.query_support.QuerySupportParams;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TestAssistantClientApiService {

	private final static Logger logger = LoggerFactory.getLogger(TestAssistantClientApiService.class);

	private static final ZAiConfig zaiConfig;

	private static final ZaiClient client;

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		zaiConfig = new ZAiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZaiClient(zaiConfig);
	}

	@Test
	@Order(1)
	public void testAssistantCompletionStream() throws JsonProcessingException {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skipping test: Using test API key, real API key required for this test");

		MessageTextContent textContent = MessageTextContent.builder()
			.text("Help me search for the release time of ZAI's CogVideoX")
			.type("text")
			.build();

		ConversationMessage messages = ConversationMessage.builder()
			.role("user")
			.content(Collections.singletonList(textContent))
			.build();

		AssistantParameters build = AssistantParameters.builder()
			.assistantId("659e54b1b8006379b4b2abd6")
			.stream(true)
			.messages(Collections.singletonList(messages))
			.build();
		// Set relevant properties of params
		AssistantApiResponse apply = client.assistants().assistantCompletionStream(build);
		if (apply.isSuccess()) {
			AtomicBoolean isFirst = new AtomicBoolean(true);
			List<MessageContent> choices = new ArrayList<>();
			AtomicReference<AssistantCompletion> lastAccumulator = new AtomicReference<>();

			apply.getFlowable().map(result -> result).doOnNext(accumulator -> {
				{
					if (isFirst.getAndSet(false)) {
						logger.info("Response: ");
					}
					MessageContent delta = accumulator.getChoices().get(0).getDelta();
					logger.info("MessageContent: {}", mapper.writeValueAsString(delta));
					choices.add(delta);
					lastAccumulator.set(accumulator);

				}
			})
				.doOnComplete(() -> System.out.println("Stream completed."))
				.doOnError(throwable -> System.err.println("Error: " + throwable)) // Handle
																					// errors
				.blockingSubscribe();// Use blockingSubscribe instead of blockingGet()

			AssistantCompletion assistantCompletion = lastAccumulator.get();

			apply.setFlowable(null);// Clear flowable before printing
			apply.setData(assistantCompletion);
		}
		logger.info("apply output: {}", mapper.writeValueAsString(apply));

	}

	@Test
	@Order(2)
	public void testQuerySupport() {
		QuerySupportParams build = QuerySupportParams.builder()
			.assistantIdList(Collections.singletonList("659e54b1b8006379b4b2abd6"))
			.build();
		AssistantSupportResponse apply = client.assistants().querySupport(build);
		logger.info("apply output: {}", apply);
	}

	@Test
	@Order(3)
	public void testQueryConversationUsage() {
		ConversationParameters build = ConversationParameters.builder().assistantId("659e54b1b8006379b4b2abd6").build();
		ConversationUsageListResponse apply = client.assistants().queryConversationUsage(build);
		logger.info("apply output: {}", apply);
	}

	@Test
	@Order(1)
	public void testTranslateAssistantCompletionStream() throws JsonProcessingException {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skipping test: Using test API key, real API key required for this test");

		MessageTextContent textContent = MessageTextContent.builder().text("Hello there").type("text").build();

		ConversationMessage messages = ConversationMessage.builder()
			.role("user")
			.content(Collections.singletonList(textContent))
			.build();

		AssistantExtraParameters assistantExtraParameters = new AssistantExtraParameters();
		TranslateParameters translateParameters = new TranslateParameters();
		translateParameters.setFrom("zh");
		translateParameters.setTo("en");
		assistantExtraParameters.setTranslate(translateParameters);
		AssistantParameters build = AssistantParameters.builder()
			.assistantId("9996ijk789lmn012o345p999")
			.stream(true)
			.messages(Collections.singletonList(messages))
			.extraParameters(assistantExtraParameters)
			.build();
		// Set relevant properties of params
		AssistantApiResponse apply = client.assistants().assistantCompletionStream(build);
		if (apply.isSuccess()) {
			AtomicBoolean isFirst = new AtomicBoolean(true);
			List<MessageContent> choices = new ArrayList<>();
			AtomicReference<AssistantCompletion> lastAccumulator = new AtomicReference<>();

			apply.getFlowable().map(result -> result).doOnNext(accumulator -> {
				{
					if (isFirst.getAndSet(false)) {
						logger.info("Response: ");
					}
					MessageContent delta = accumulator.getChoices().get(0).getDelta();
					logger.info("MessageContent: {}", mapper.writeValueAsString(delta));
					choices.add(delta);
					lastAccumulator.set(accumulator);

				}
			})
				.doOnComplete(() -> System.out.println("Stream completed."))
				.doOnError(throwable -> System.err.println("Error: " + throwable)) // Handle
																					// errors
				.blockingSubscribe();// Use blockingSubscribe instead of blockingGet()

			AssistantCompletion assistantCompletion = lastAccumulator.get();

			apply.setFlowable(null);// Clear flowable before printing
			apply.setData(assistantCompletion);
		}
		logger.info("apply output: {}", mapper.writeValueAsString(apply));
	}

	@Test
	public void testTranslateAssistantCompletion() throws JsonProcessingException {
		MessageTextContent textContent = MessageTextContent.builder().text("Hello there").type("text").build();

		ConversationMessage messages = ConversationMessage.builder()
			.role("user")
			.content(Collections.singletonList(textContent))
			.build();

		AssistantExtraParameters assistantExtraParameters = new AssistantExtraParameters();
		assistantExtraParameters.setTranslate(TranslateParameters.builder().from("zh").to("en").build());

		AssistantParameters build = AssistantParameters.builder()
			.assistantId("9996ijk789lmn012o345p999")
			.stream(false)
			.messages(Collections.singletonList(messages))
			.extraParameters(assistantExtraParameters)
			.build();

		// Set relevant properties of params
		AssistantApiResponse apply = client.assistants().assistantCompletion(build);
		logger.info("model output: {}", mapper.writeValueAsString(apply));
	}

}
