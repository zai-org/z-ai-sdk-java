package ai.z.openapi;

import ai.z.openapi.core.Constants;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatTool;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.WebSearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ai.z.openapi.mock.MockClient;
import ai.z.openapi.service.web_search.WebSearchRequest;
import ai.z.openapi.service.web_search.WebSearchResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Testcontainers
public class WebSearchTest {

	private final static Logger logger = LoggerFactory.getLogger(WebSearchTest.class);

	private static final ZaiConfig zaiConfig;

	private static final ZaiClient client;

	private static final String requestIdTemplate = "mycompany-%d";

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZaiClient(zaiConfig);
	}

	/**
	 * Web search tool capability test
	 */
	@Test
	public void testWebSearch() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			WebSearchRequest webSearchRequest = WebSearchRequest.builder()
				.searchEngine("search_std")
				.searchQuery("How is the weather in Beijing today")
				.searchEngine("search_std")
				.count(50)
				.searchDomainFilter("finance.sina.com.cn")
				.searchRecencyFilter("oneYear")
				.contentSize("high")
				.requestId("11111111")
				.build();

			// Use mock data
			WebSearchResponse webSearchResponse = MockClient.mockWebSearch(webSearchRequest);
			logger.info("Mock webSearch response: {}", mapper.writeValueAsString(webSearchResponse));
			return;
		}

		WebSearchRequest webSearchRequest = WebSearchRequest.builder()
			.searchEngine("search_std")
			.searchQuery("How is the weather in Beijing today")
			.searchEngine("search_std")
			.count(50)
			.searchDomainFilter("finance.sina.com.cn")
			.searchRecencyFilter("oneYear")
			.contentSize("high")
			.requestId("11111111")
			.build();

		WebSearchResponse webSearchResponse = client.webSearch().createWebSearch(webSearchRequest);
		logger.info("webSearch output: {}", mapper.writeValueAsString(webSearchResponse));
	}

	/**
	 * Large model + web_search tool capability test
	 */
	@Test
	public void testV4ChatWithWebSearch() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			List<ChatMessage> messages = new ArrayList<>();
			ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(),
					"How much tariff did Trump impose on China in 2025");
			messages.add(chatMessage);

			ChatTool webSearchTool = new ChatTool();
			webSearchTool.setType("web_search");
			WebSearch webSearch = new WebSearch();
			webSearch.setEnable(Boolean.TRUE);
			webSearch.setSearchEngine("search_std");
			webSearch.setSearchResult(Boolean.TRUE);
			webSearchTool.setWebSearch(webSearch);

			String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
			Map<String, Object> extraJson = new HashMap<>();
			extraJson.put("invoke_method", Constants.INVOKE_METHOD);
			ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
				.model(Constants.ModelChatGLM4)
				.stream(Boolean.FALSE)
				.messages(messages)
				.requestId(requestId)
				.extraJson(extraJson)
				.tools(Collections.singletonList(webSearchTool))
				.build();

			// Use mock data
			ChatCompletionResponse modelApiResp = MockClient.mockModelApi(chatCompletionRequest);
			logger.info("Mock model response: {}", mapper.writeValueAsString(modelApiResp));
			return;
		}

		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(),
				"How much tariff did Trump impose on China in 2025");
		messages.add(chatMessage);

		ChatTool webSearchTool = new ChatTool();
		webSearchTool.setType("web_search");
		WebSearch webSearch = new WebSearch();
		webSearch.setEnable(Boolean.TRUE);
		webSearch.setSearchEngine("search_std");
		webSearch.setSearchResult(Boolean.TRUE);
		webSearchTool.setWebSearch(webSearch);

		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
		Map<String, Object> extraJson = new HashMap<>();
		extraJson.put("invoke_method", Constants.INVOKE_METHOD);
		ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.extraJson(extraJson)
			.tools(Collections.singletonList(webSearchTool))
			.build();
		ChatCompletionResponse modelApiResp = client.chat().createChatCompletion(chatCompletionRequest);
		logger.info("model output: {}", mapper.writeValueAsString(modelApiResp));
	}

	@Test
	public void testV4ChatWithWebSearchSSE() {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			List<ChatMessage> messages = new ArrayList<>();
			ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(),
					"How much tariff did Trump impose on China in 2025");
			messages.add(chatMessage);

			ChatTool webSearchTool = new ChatTool();
			webSearchTool.setType("web_search");
			WebSearch webSearch = new WebSearch();
			webSearch.setEnable(Boolean.TRUE);
			webSearch.setSearchEngine("search_std");
			webSearch.setResultSequence("before");
			webSearch.setSearchResult(Boolean.TRUE);
			webSearchTool.setWebSearch(webSearch);

			String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
			ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
				.model(Constants.ModelChatGLM4)
				.stream(Boolean.TRUE)
				.messages(messages)
				.requestId(requestId)
				.tools(Collections.singletonList(webSearchTool))
				.build();

			// Use mock data
			ChatCompletionResponse sseModelApiResp = MockClient.mockModelApi(chatCompletionRequest);
			if (sseModelApiResp.isSuccess()) {
				sseModelApiResp.getFlowable().doOnNext(modelData -> {
					logger.info("Mock model streaming response: {}", mapper.writeValueAsString(modelData));
				}).blockingSubscribe();
			}
			return;
		}

		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(),
				"How much tariff did Trump impose on China in 2025");
		messages.add(chatMessage);

		ChatTool webSearchTool = new ChatTool();
		webSearchTool.setType("web_search");
		WebSearch webSearch = new WebSearch();
		webSearch.setEnable(Boolean.TRUE);
		webSearch.setSearchEngine("search_std");
		webSearch.setResultSequence("before");
		webSearch.setSearchResult(Boolean.TRUE);
		webSearchTool.setWebSearch(webSearch);

		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
		ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.TRUE)
			.messages(messages)
			.requestId(requestId)
			.tools(Collections.singletonList(webSearchTool))
			.build();
		ChatCompletionResponse sseModelApiResp = client.chat().createChatCompletion(chatCompletionRequest);
		if (sseModelApiResp.isSuccess()) {
			sseModelApiResp.getFlowable().doOnNext(modelData -> {
				logger.info("model output: {}", mapper.writeValueAsString(modelData));
			}).blockingSubscribe();
		}
	}

}
