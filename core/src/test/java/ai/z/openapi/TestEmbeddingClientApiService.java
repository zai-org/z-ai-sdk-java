package ai.z.openapi;

import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.mock.MockClient;
import ai.z.openapi.service.embedding.Embedding;
import ai.z.openapi.service.embedding.EmbeddingCreateParams;
import ai.z.openapi.service.embedding.EmbeddingResponse;
import ai.z.openapi.service.embedding.EmbeddingResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

@Testcontainers
public class TestEmbeddingClientApiService {

	private final static Logger logger = LoggerFactory.getLogger(TestEmbeddingClientApiService.class);

	private static final ZaiConfig zaiConfig;

	private static final ZaiClient client;

	static {
		zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZaiClient(zaiConfig);
	}

	@Test
	public void testEmbedding() {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			EmbeddingCreateParams world = EmbeddingCreateParams.builder()
				.input("hello world")
				.dimensions(512)
				.model("embedding-3")
				.build();
			// Use mock data
			EmbeddingResponse embeddingApiResponse = MockClient.mockEmbeddingApi(world);
			EmbeddingResult data = embeddingApiResponse.getData();
			List<Embedding> data1 = data.getData();
			data1.forEach(embedding -> {
				logger.info("Mock embedding: {}", embedding.getEmbedding());
				assert embedding.getEmbedding().size() == 512;
			});
			logger.info("Mock embedding response: {}", embeddingApiResponse);
			return;
		}

		EmbeddingCreateParams world = EmbeddingCreateParams.builder()
			.input("hello world")
			.dimensions(512)
			.model("embedding-3")
			.build();
		EmbeddingResponse embeddingApiResponse = client.embeddings().createEmbeddings(world);
		EmbeddingResult data = embeddingApiResponse.getData();
		List<Embedding> data1 = data.getData();
		data1.forEach(embedding -> {
			logger.info("embedding:{}", embedding.getEmbedding());
			assert embedding.getEmbedding().size() == 512;
		});
		logger.info("apply:{}", embeddingApiResponse);
	}

	@Test
	public void testEmbeddingList() {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");

			EmbeddingCreateParams world = EmbeddingCreateParams.builder()
				.input(Arrays.asList("hello world", "hello world"))
				.dimensions(512)
				.model("embedding-3")
				.build();

			// Use mock data
			EmbeddingResponse embeddingApiResponse = MockClient.mockEmbeddingApi(world);
			EmbeddingResult data = embeddingApiResponse.getData();
			List<Embedding> data1 = data.getData();
			data1.forEach(embedding -> {
				logger.info("Mock embedding list: {}", embedding.getEmbedding());
				assert embedding.getEmbedding().size() == 512;
			});
			logger.info("Mock embedding list response: {}", embeddingApiResponse);
			return;
		}

		EmbeddingCreateParams world = EmbeddingCreateParams.builder()
			.input(Arrays.asList("hello world", "hello world"))
			.dimensions(512)
			.model("embedding-3")
			.build();
		EmbeddingResponse embeddingApiResponse = client.embeddings().createEmbeddings(world);
		EmbeddingResult data = embeddingApiResponse.getData();
		List<Embedding> data1 = data.getData();
		data1.forEach(embedding -> {
			logger.info("embedding:{}", embedding.getEmbedding());
			assert embedding.getEmbedding().size() == 512;
		});
		logger.info("apply:{}", embeddingApiResponse);
	}

}
