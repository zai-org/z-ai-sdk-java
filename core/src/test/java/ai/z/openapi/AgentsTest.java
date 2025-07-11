package ai.z.openapi;

import ai.z.openapi.core.config.ZAiConfig;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatCompletionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ai.z.openapi.service.agents.AgentsCompletionRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

@Testcontainers
public class AgentsTest {

	private final static Logger logger = LoggerFactory.getLogger(AgentsTest.class);

	private static final ZAiConfig zaiConfig;

	private static final ZAiClient client;

	// 请自定义自己的业务id
	private static final String requestIdTemplate = "mycompany-%d";

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		zaiConfig = new ZAiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZAiClient(zaiConfig);
	}

	@Test
	public void testAgentsSyncInvoke() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");
			ChatCompletionResponse mockResponse = new ChatCompletionResponse();
			mockResponse.setCode(200);
			mockResponse.setMsg("success");
			mockResponse.setSuccess(true);
			logger.info("Mock response: {}", mapper.writeValueAsString(mockResponse));
			return;
		}

		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "hello");
		messages.add(chatMessage);
		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

		AgentsCompletionRequest chatCompletionRequest = AgentsCompletionRequest.builder()
			.agent_id("general_translation")
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();
		ChatCompletionResponse invokeModelApiResp = client.agents().createAgentCompletion(chatCompletionRequest);
		logger.info("model output: {}", mapper.writeValueAsString(invokeModelApiResp));
	}

	@Test
	public void testAgentsSSEInvoke() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");
			ChatCompletionResponse mockResponse = new ChatCompletionResponse();
			mockResponse.setCode(200);
			mockResponse.setMsg("success");
			mockResponse.setSuccess(true);
			mockResponse.setFlowable(null);// Clear flowable before printing
			logger.info("Mock response: {}", mapper.writeValueAsString(mockResponse));
			return;
		}

		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "hello");
		messages.add(chatMessage);
		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

		AgentsCompletionRequest chatCompletionRequest = AgentsCompletionRequest.builder()
			.agent_id("general_translation")
			.stream(Boolean.TRUE)
			.messages(messages)
			.requestId(requestId)
			.build();
		ChatCompletionResponse invokeModelApiResp = client.agents().createAgentCompletion(chatCompletionRequest);
		invokeModelApiResp.getFlowable()
			.doOnNext(modelData -> logger.info("modelData: {}", modelData.toString()))
			.blockingSubscribe();

	}

}
