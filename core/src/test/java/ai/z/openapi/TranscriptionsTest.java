package ai.z.openapi;

import ai.z.openapi.service.model.ChatCompletionResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import ai.z.openapi.mock.MockClient;
import ai.z.openapi.core.config.ZAiConfig;
import ai.z.openapi.service.audio.AudioTranscriptionsRequest;
import ai.z.openapi.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

@Testcontainers
public class TranscriptionsTest {

	private final static Logger logger = LoggerFactory.getLogger(TranscriptionsTest.class);

	private static final ZAiConfig zaiConfig;

	private static final ZAiClient client;

	private static final ObjectMapper mapper = new ObjectMapper();
	static {
		zaiConfig = new ZAiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZAiClient(zaiConfig);
	}

	public static ObjectMapper defaultObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		return mapper;
	}

	/**
	 * SSE-V4: Function calling
	 */
	@Test
	public void testInvokeTranscriptions() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			AudioTranscriptionsRequest audioTranscriptionsRequest = new AudioTranscriptionsRequest();
			audioTranscriptionsRequest.setFile(new java.io.File("src/test/resources/asr.wav"));
			audioTranscriptionsRequest.setModel("glm-asr");
			audioTranscriptionsRequest.setStream(false);

			// Use mock data
			ChatCompletionResponse mockResponse = MockClient.mockTranscriptionsApi(audioTranscriptionsRequest);
			logger.info("Mock transcription response: {}", mapper.writeValueAsString(mockResponse));
			return;
		}

		AudioTranscriptionsRequest audioTranscriptionsRequest = new AudioTranscriptionsRequest();
		audioTranscriptionsRequest.setFile(new java.io.File("src/test/resources/asr.wav"));
		audioTranscriptionsRequest.setModel("glm-asr");
		audioTranscriptionsRequest.setStream(false);
		ChatCompletionResponse modelApiResp = client.audio().createTranscription(audioTranscriptionsRequest);
		logger.info("testInvokeTranscriptions output: {}", mapper.writeValueAsString(modelApiResp));
	}

	@Test
	public void testSSEInvokeTranscriptions() {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			AudioTranscriptionsRequest audioTranscriptionsRequest = new AudioTranscriptionsRequest();
			audioTranscriptionsRequest.setFile(new java.io.File("src/test/resources/asr.webm"));
			audioTranscriptionsRequest.setModel("glm-asr");
			audioTranscriptionsRequest.setStream(true);

			// Use mock data
			ChatCompletionResponse mockResponse = MockClient.mockTranscriptionsApi(audioTranscriptionsRequest);
			logger.info("Mock SSE transcription response: {}", mockResponse);
			return;
		}

		AudioTranscriptionsRequest audioTranscriptionsRequest = new AudioTranscriptionsRequest();
		audioTranscriptionsRequest.setFile(new java.io.File("src/test/resources/asr.webm"));
		audioTranscriptionsRequest.setModel("glm-asr");
		audioTranscriptionsRequest.setStream(true);
		ChatCompletionResponse sseModelApiResp = client.audio().createTranscription(audioTranscriptionsRequest);
		if (sseModelApiResp.isSuccess()) {
			sseModelApiResp.getFlowable().doOnNext(modelData -> {
				logger.info("modelData:{}", modelData);
			})
				.doOnComplete(() -> System.out.println("Stream completed."))
				.doOnError(throwable -> System.err.println("Error: " + throwable))
				.blockingSubscribe();
		}
	}

}
