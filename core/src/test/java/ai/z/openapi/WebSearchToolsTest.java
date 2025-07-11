package ai.z.openapi;

import ai.z.openapi.core.config.ZAiConfig;
import ai.z.openapi.service.tools.ChoiceDelta;
import ai.z.openapi.service.tools.SearchChatMessage;
import ai.z.openapi.service.tools.WebSearchApiResponse;
import ai.z.openapi.service.tools.WebSearchParamsRequest;
import ai.z.openapi.service.tools.WebSearchPro;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ai.z.openapi.mock.MockClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Testcontainers
public class WebSearchToolsTest {

	private final static Logger logger = LoggerFactory.getLogger(WebSearchToolsTest.class);

	private static final ZAiConfig zaiConfig;

	private static final ZAiClient client;

	static {
		zaiConfig = new ZAiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZAiClient(zaiConfig);
	}
	private static final ObjectMapper mapper = new ObjectMapper();

	// Please customize your own business ID
	private static final String requestIdTemplate = "mycompany-%d";

	@Test
	public void test1() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			// Use mock data for testing
			String jsonString = "[\n" + "                {\n" + "                    \"content\": \"Hello\",\n"
					+ "                    \"role\": \"user\"\n" + "                }\n" + "            ]";

			ArrayList<SearchChatMessage> messages = new ObjectMapper().readValue(jsonString,
					new TypeReference<ArrayList<SearchChatMessage>>() {
					});

			String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
			WebSearchParamsRequest chatCompletionRequest = WebSearchParamsRequest.builder()
				.model("web-search-pro")
				.stream(Boolean.TRUE)
				.messages(messages)
				.requestId(requestId)
				.build();

			// Use mock data
			WebSearchApiResponse webSearchApiResponse = MockClient
				.mockWebSearchProStreamingInvoke(chatCompletionRequest);
			webSearchApiResponse.setFlowable(null);// Clear flowable before printing
			logger.info("Mock response: {}", mapper.writeValueAsString(webSearchApiResponse));
			return;
		}

		// JSON conversion to ArrayList<SearchChatMessage>
		String jsonString = "[\n" + "                {\n" + "                    \"content\": \"Hello\",\n"
				+ "                    \"role\": \"user\"\n" + "                }\n" + "            ]";

		ArrayList<SearchChatMessage> messages = new ObjectMapper().readValue(jsonString,
				new TypeReference<ArrayList<SearchChatMessage>>() {
				});

		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
		WebSearchParamsRequest chatCompletionRequest = WebSearchParamsRequest.builder()
			.model("web-search-pro")
			.stream(Boolean.TRUE)
			.messages(messages)
			.requestId(requestId)
			.build();
		WebSearchApiResponse webSearchApiResponse = client.webSearch().createWebSearchProStream(chatCompletionRequest);
		if (webSearchApiResponse.isSuccess()) {
			AtomicBoolean isFirst = new AtomicBoolean(true);
			List<ChoiceDelta> choices = new ArrayList<>();
			AtomicReference<WebSearchPro> lastAccumulator = new AtomicReference<>();

			webSearchApiResponse.getFlowable().map(result -> result).doOnNext(accumulator -> {
				{
					if (isFirst.getAndSet(false)) {
						logger.info("Response: ");
					}
					ChoiceDelta delta = accumulator.getChoices().get(0).getDelta();
					if (delta != null && delta.getToolCalls() != null) {
						logger.info("tool_calls: {}", mapper.writeValueAsString(delta.getToolCalls()));
					}
					choices.add(delta);
					lastAccumulator.set(accumulator);

				}
			})
				.doOnComplete(() -> System.out.println("Stream completed."))
				.doOnError(throwable -> System.err.println("Error: " + throwable)) // Handle
																					// errors
				.blockingSubscribe();// Use blockingSubscribe instead of blockingGet()

			WebSearchPro chatMessageAccumulator = lastAccumulator.get();

			webSearchApiResponse.setFlowable(null);// Clear flowable before printing
			webSearchApiResponse.setData(chatMessageAccumulator);
		}
		logger.info("model output: {}", mapper.writeValueAsString(webSearchApiResponse));
		client.close();
		// List all active threads
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			logger.info("Thread: " + t.getName() + " State: " + t.getState());
		}
	}

	@Test
	public void test2() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			// Use mock data for testing
			String jsonString = "[\n" + "                {\n" + "                    \"content\": \"Hello\",\n"
					+ "                    \"role\": \"user\"\n" + "                }\n" + "            ]";

			ArrayList<SearchChatMessage> messages = new ObjectMapper().readValue(jsonString,
					new TypeReference<ArrayList<SearchChatMessage>>() {
					});

			String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
			WebSearchParamsRequest chatCompletionRequest = WebSearchParamsRequest.builder()
				.model("web-search-pro")
				.stream(Boolean.FALSE)
				.messages(messages)
				.requestId(requestId)
				.build();

			// Use mock data
			WebSearchApiResponse webSearchApiResponse = MockClient.mockWebSearchProInvoke(chatCompletionRequest);
			logger.info("Mock response: {}", mapper.writeValueAsString(webSearchApiResponse));
			return;
		}

		// JSON conversion to ArrayList<SearchChatMessage>
		String jsonString = "[\n" + "                {\n" + "                    \"content\": \"Hello\",\n"
				+ "                    \"role\": \"user\"\n" + "                }\n" + "            ]";

		ArrayList<SearchChatMessage> messages = new ObjectMapper().readValue(jsonString,
				new TypeReference<ArrayList<SearchChatMessage>>() {
				});

		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
		WebSearchParamsRequest chatCompletionRequest = WebSearchParamsRequest.builder()
			.model("web-search-pro")
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.build();
		WebSearchApiResponse webSearchApiResponse = client.webSearch().createWebSearchPro(chatCompletionRequest);

		logger.info("model output: {}", mapper.writeValueAsString(webSearchApiResponse));

	}

}