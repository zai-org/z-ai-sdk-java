package ai.z.openapi.service.web_search;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.tools.SearchChatMessage;
import ai.z.openapi.service.tools.WebSearchApiResponse;
import ai.z.openapi.service.tools.WebSearchParamsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WebSearchService test class for testing various functionalities of WebSearchService and
 * WebSearchServiceImpl
 */
@DisplayName("WebSearchService Tests")
public class WebSearchServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(WebSearchServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private WebSearchService webSearchService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "web-search-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		webSearchService = client.webSearch();
	}

	@Test
	@DisplayName("Test WebSearchService Instantiation")
	void testWebSearchServiceInstantiation() {
		assertNotNull(webSearchService, "WebSearchService should be properly instantiated");
		assertInstanceOf(WebSearchServiceImpl.class, webSearchService,
				"WebSearchService should be an instance of WebSearchServiceImpl");
	}

	@Test
	@DisplayName("Test Web Search Pro - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testWebSearchPro() throws JsonProcessingException {
		// Prepare test data
		List<SearchChatMessage> messages = new ArrayList<>();
		SearchChatMessage userMessage = new SearchChatMessage(ChatMessageRole.USER.value(),
				"Please search for the latest AI technology trends");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		WebSearchParamsRequest request = WebSearchParamsRequest.builder()
			.model("web-search-pro")
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Execute test
		WebSearchApiResponse response = webSearchService.createWebSearchPro(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNull(response.getError(), "Response error should be null");
		assertNotNull(response.getData().getChoices(),
				"Multi-query web search response data choices should not be null");
		assertFalse(response.getData().getChoices().isEmpty(),
				"Multi-query web search response data choices should not be empty");
		assertNotNull(response.getData().getChoices().get(0).getMessage(),
				"Multi-query web search response data choices message should not be null");
		logger.info("Web search pro response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Web Search Pro Stream")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testWebSearchProStream() throws JsonProcessingException {
		// Prepare test data
		List<SearchChatMessage> messages = new ArrayList<>();
		SearchChatMessage userMessage = new SearchChatMessage(ChatMessageRole.USER.value(),
				"Search for recent developments in machine learning");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		WebSearchParamsRequest request = WebSearchParamsRequest.builder()
			.model("web-search-pro")
			.stream(Boolean.TRUE)
			.messages(messages)
			.requestId(requestId)
			.build();

		// Execute test
		WebSearchApiResponse response = webSearchService.createWebSearchProStream(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");

		// Test stream data processing
		AtomicInteger messageCount = new AtomicInteger(0);
		AtomicBoolean isFirst = new AtomicBoolean(true);

		response.getFlowable().doOnNext(webSearchPro -> {
			if (isFirst.getAndSet(false)) {
				logger.info("Starting to receive stream response:");
			}
			if (webSearchPro.getChoices() != null && !webSearchPro.getChoices().isEmpty()) {
				assertNotNull(webSearchPro.getChoices().get(0).getDelta(), "Delta should not be null");
				logger.info("Received web search result: {}", webSearchPro.getChoices().get(0));
				messageCount.incrementAndGet();
			}
		})
			.doOnComplete(
					() -> logger.info("Stream response completed, received {} messages in total", messageCount.get()))
			.blockingSubscribe();

		assertTrue(messageCount.get() > 0, "Should receive at least one message");

		logger.info("Stream web search pro test completed");
	}

	@Test
	@DisplayName("Test Basic Web Search")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testBasicWebSearch() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		WebSearchRequest request = WebSearchRequest.builder()
			.searchEngine("search_std")
			.searchQuery("artificial intelligence latest news")
			.count(2)
			.searchRecencyFilter("oneYear")
			.contentSize("high")
			.requestId(requestId)
			.build();

		// Execute test
		WebSearchResponse response = webSearchService.createWebSearch(request);

		// Verify results
		assertNotNull(response, "Basic web search response should not be null");
		assertTrue(response.isSuccess(), "Basic web search response should be successful");
		assertNotNull(response.getData(), "Basic web search response data should not be null");
		assertNull(response.getError(), "Basic web search response error should be null");
		assertNotNull(response.getData().getWebSearchResp(), "Multi-query web search response data should not be null");
		assertFalse(response.getData().getWebSearchResp().isEmpty(),
				"Multi-query web search response data should not be empty");
		logger.info("Basic web search response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Web Search Pro with Standard Model")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testWebSearchProStandardModel() throws JsonProcessingException {
		List<SearchChatMessage> messages = new ArrayList<>();
		SearchChatMessage userMessage = new SearchChatMessage(ChatMessageRole.USER.value(),
				"Search for technology news");
		messages.add(userMessage);

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		WebSearchParamsRequest request = WebSearchParamsRequest.builder()
			.model("web-search-pro")
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		WebSearchApiResponse response = webSearchService.createWebSearchPro(request);

		assertNotNull(response, "Response should not be null");
		logger.info("Web search pro response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Web Search Pro")
	void testValidation_NullWebSearchProRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			webSearchService.createWebSearchPro(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Basic Web Search")
	void testValidation_NullBasicWebSearchRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			webSearchService.createWebSearch(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Multi-Query Web Search")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testMultiQueryWebSearch() throws JsonProcessingException {
		List<SearchChatMessage> messages = new ArrayList<>();

		// First query
		messages.add(new SearchChatMessage(ChatMessageRole.USER.value(), "Search for AI technology trends"));
		messages.add(new SearchChatMessage(ChatMessageRole.ASSISTANT.value(),
				"I found some information about AI technology trends..."));

		// Second query
		messages.add(new SearchChatMessage(ChatMessageRole.USER.value(), "Now search for machine learning frameworks"));

		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		WebSearchParamsRequest request = WebSearchParamsRequest.builder()
			.model("web-search-pro")
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();

		WebSearchApiResponse response = webSearchService.createWebSearchPro(request);

		assertNotNull(response, "Multi-query web search response should not be null");
		assertTrue(response.isSuccess(), "Multi-query web search response should be successful");
		assertNotNull(response.getData(), "Multi-query web search response data should not be null");
		assertNull(response.getError(), "Multi-query web search response error should be null");
		assertNotNull(response.getData().getChoices(),
				"Multi-query web search response data choices should not be null");
		assertFalse(response.getData().getChoices().isEmpty(),
				"Multi-query web search response data choices should not be empty");
		assertNotNull(response.getData().getChoices().get(0).getMessage(),
				"Multi-query web search response data choices message should not be null");
		logger.info("Multi-query web search response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Web Search with Different Search Engines")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDifferentSearchEngines() throws JsonProcessingException {
		String[] searchEngines = { "search_std" };

		for (String searchEngine : searchEngines) {
			String requestId = String.format(REQUEST_ID_TEMPLATE + "-%s", System.currentTimeMillis(), searchEngine);

			WebSearchRequest request = WebSearchRequest.builder()
				.searchEngine(searchEngine)
				.searchQuery("artificial intelligence")
				.count(2)
				.searchRecencyFilter("oneYear")
				.contentSize("high")
				.requestId(requestId)
				.build();

			WebSearchResponse response = webSearchService.createWebSearch(request);

			assertNotNull(response, String.format("Response for %s should not be null", searchEngine));
			assertTrue(response.isSuccess(), "Multi-query web search response should be successful");
			assertNotNull(response.getData(), "Multi-query web search response data should not be null");
			assertNull(response.getError(), "Multi-query web search response error should be null");
			assertNotNull(response.getData().getWebSearchResp(),
					"Multi-query web search response data should not be null");
			assertFalse(response.getData().getWebSearchResp().isEmpty(),
					"Multi-query web search response data should not be empty");

			logger.info("Search engine {} response: {}", searchEngine, mapper.writeValueAsString(response));
		}
	}

}
