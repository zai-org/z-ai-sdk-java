package ai.z.openapi;

import ai.z.openapi.core.Constants;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.Usage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatTool;
import ai.z.openapi.service.model.Choice;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ModelData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ai.z.openapi.mock.MockClient;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import io.reactivex.Flowable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class AllToolsTest {

	private final static Logger logger = LoggerFactory.getLogger(AllToolsTest.class);

	private static final ZaiConfig zaiConfig;

	private static final ZaiClient client;

	private static final ObjectMapper mapper = MessageDeserializeFactory.defaultObjectMapper();

	// Please customize your own business ID
	private static final String requestIdTemplate = "mycompany-%d";

	static {
		zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZaiClient(zaiConfig);
	}

	@Test
	public void test1() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			List<ChatMessage> messages = new ArrayList<>();
			ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "Help me check Beijing weather");
			messages.add(chatMessage);
			String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

			Map<String, Object> extraJson = new HashMap<>();
			extraJson.put("invoke_method", Constants.INVOKE_METHOD);

			ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
				.model("glm-4-alltools")
				.stream(Boolean.TRUE)
				.extraJson(extraJson)
				.messages(messages)
				.requestId(requestId)
				.build();

			// Use mock data
			ChatCompletionResponse mockResponse = MockClient.mockModelApi(chatCompletionRequest);
			logger.info("Mock AllTools response: {}", mockResponse);
			return;
		}

		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "Help me check Beijing weather");
		messages.add(chatMessage);
		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
		// Function call parameter construction
		List<ChatTool> chatToolList = new ArrayList<>();
		ChatTool chatTool = new ChatTool();

		chatTool.setType("code_interpreter");
		chatToolList.add(chatTool);

		Map<String, Object> extraJson = new HashMap<>();
		extraJson.put("invoke_method", Constants.INVOKE_METHOD);

		ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
			.model("glm-4-alltools")
			.stream(Boolean.TRUE)
			.extraJson(extraJson)
			.messages(messages)
			.tools(chatToolList)
			.toolChoice("auto")
			.requestId(requestId)
			.build();
		ChatCompletionResponse sseModelApiResp = client.chat().createChatCompletion(chatCompletionRequest);
		if (sseModelApiResp.isSuccess()) {
			AtomicBoolean isFirst = new AtomicBoolean(true);
			List<Choice> choices = new ArrayList<>();
			AtomicReference<Usage> lastUsage = new AtomicReference<>();
			AtomicReference<String> lastId = new AtomicReference<>();
			AtomicReference<Long> lastCreated = new AtomicReference<>();

			sseModelApiResp.getFlowable().doOnNext(modelData -> {
				{
					if (isFirst.getAndSet(false)) {
						logger.info("Response: ");
					}
					if (modelData.getChoices() != null && !modelData.getChoices().isEmpty()) {
						Choice choice = modelData.getChoices().get(0);
						if (choice.getDelta() != null && choice.getDelta().getTool_calls() != null) {
							String jsonString = mapper.writeValueAsString(choice.getDelta().getTool_calls());
							logger.info("tool_calls: {}", jsonString);
						}
						if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
							logger.info(choice.getDelta().getContent());
						}
						choices.add(choice);
					}
					if (modelData.getUsage() != null) {
						lastUsage.set(modelData.getUsage());
					}
					if (modelData.getId() != null) {
						lastId.set(modelData.getId());
					}
					if (modelData.getCreated() != null) {
						lastCreated.set(modelData.getCreated());
					}
				}
			})
				.doOnComplete(() -> System.out.println("Stream completed."))
				.doOnError(throwable -> System.err.println("Error: " + throwable)) // Handle
																					// errors
				.blockingSubscribe();// Use blockingSubscribe instead of blockingGet()

			ModelData data = new ModelData();
			data.setChoices(choices);
			data.setUsage(lastUsage.get());
			data.setId(lastId.get());
			data.setCreated(lastCreated.get());
			data.setRequestId(chatCompletionRequest.getRequestId());
			sseModelApiResp.setFlowable(null);// Clear flowable before printing
			sseModelApiResp.setData(data);
		}
		logger.info("model output: {}", mapper.writeValueAsString(sseModelApiResp));
		client.close();
		// List all active threads
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			logger.info("Thread: " + t.getName() + " State: " + t.getState());
		}
	}

}