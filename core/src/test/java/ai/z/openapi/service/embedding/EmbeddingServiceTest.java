package ai.z.openapi.service.embedding;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.core.config.ZaiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EmbeddingService and EmbeddingServiceImpl. Tests cover service
 * instantiation, embedding creation, and parameter validation.
 */
class EmbeddingServiceTest {

	private EmbeddingService embeddingService;

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		embeddingService = client.embeddings();
	}

	@Test
	void testEmbeddingServiceInstantiation() {
		assertNotNull(embeddingService);
		assertInstanceOf(EmbeddingServiceImpl.class, embeddingService);
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateEmbeddingsWithString() {

		EmbeddingCreateParams request = EmbeddingCreateParams.builder().model(Constants.ModelEmbedding3).build();
		request.setInput("Hello, world!");

		EmbeddingResponse response = embeddingService.createEmbeddings(request);

		assertNotNull(response);
		assertNotNull(response.getData());
		assertNotNull(response.getData().getData());
		assertFalse(response.getData().getData().isEmpty());
		assertNotNull(response.getData().getData().get(0).getEmbedding());
		assertFalse(response.getData().getData().get(0).getEmbedding().isEmpty());
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateEmbeddingsWithStringList() {

		List<String> inputs = Arrays.asList("Hello, world!", "How are you?", "Nice to meet you!");

		EmbeddingCreateParams request = EmbeddingCreateParams.builder().model(Constants.ModelEmbedding3).build();
		request.setInput(inputs);

		EmbeddingResponse response = embeddingService.createEmbeddings(request);

		assertNotNull(response);
		assertNotNull(response.getData());
		assertNotNull(response.getData().getData());
		assertEquals(3, response.getData().getData().size());
		for (Embedding embedding : response.getData().getData()) {
			assertNotNull(embedding.getEmbedding());
			assertFalse(embedding.getEmbedding().isEmpty());
		}
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateEmbeddingsWithEmbedding2Model() {

		EmbeddingCreateParams request = EmbeddingCreateParams.builder().model(Constants.ModelEmbedding2).build();
		request.setInput("Test embedding with model 2");

		EmbeddingResponse response = embeddingService.createEmbeddings(request);

		assertNotNull(response);
		assertNotNull(response.getData());
		assertNotNull(response.getData().getData());
		assertFalse(response.getData().getData().isEmpty());
		assertNotNull(response.getData().getData().get(0).getEmbedding());
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateEmbeddingsWithDimensions() {

		EmbeddingCreateParams request = EmbeddingCreateParams.builder()
			.model(Constants.ModelEmbedding3)
			.dimensions(512)
			.build();
		request.setInput("Test with custom dimensions");

		EmbeddingResponse response = embeddingService.createEmbeddings(request);

		assertNotNull(response);
		assertNotNull(response.getData());
		assertNotNull(response.getData().getData());
		assertFalse(response.getData().getData().isEmpty());
		assertNotNull(response.getData().getData().get(0).getEmbedding());
	}

	@Test
	void testCreateEmbeddingsWithNullRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			embeddingService.createEmbeddings(null);
		});
	}

	@Test
	void testCreateEmbeddingsWithNullModel() {
		EmbeddingCreateParams request = EmbeddingCreateParams.builder().build();
		request.setInput("Test input");

		assertThrows(IllegalArgumentException.class, () -> {
			embeddingService.createEmbeddings(request);
		});
	}

	@Test
	void testCreateEmbeddingsWithEmptyModel() {
		EmbeddingCreateParams request = EmbeddingCreateParams.builder().model("").build();
		request.setInput("Test input");

		assertThrows(IllegalArgumentException.class, () -> {
			embeddingService.createEmbeddings(request);
		});
	}

	@Test
	void testCreateEmbeddingsWithNullInput() {
		EmbeddingCreateParams request = EmbeddingCreateParams.builder().model(Constants.ModelEmbedding3).build();
		// input is null by default

		assertThrows(IllegalArgumentException.class, () -> {
			embeddingService.createEmbeddings(request);
		});
	}

	@Test
	void testCreateEmbeddingsWithEmptyStringInput() {
		EmbeddingCreateParams request = EmbeddingCreateParams.builder().model(Constants.ModelEmbedding3).build();
		request.setInput("");

		assertThrows(IllegalArgumentException.class, () -> {
			embeddingService.createEmbeddings(request);
		});
	}

	@Test
	void testCreateEmbeddingsWithEmptyListInput() {
		EmbeddingCreateParams request = EmbeddingCreateParams.builder().model(Constants.ModelEmbedding3).build();

		assertThrows(IllegalArgumentException.class, () -> {
			request.setInput(Arrays.asList());
		});
	}

	@Test
	void testCreateEmbeddingsWithNullListInput() {
		EmbeddingCreateParams request = EmbeddingCreateParams.builder().model(Constants.ModelEmbedding3).build();

		assertThrows(IllegalArgumentException.class, () -> {
			request.setInput((List<String>) null);
		});
	}

	@Test
	void testCreateEmbeddingsWithNegativeDimensions() {
		EmbeddingCreateParams request = EmbeddingCreateParams.builder()
			.model(Constants.ModelEmbedding3)
			.dimensions(-1)
			.build();
		request.setInput("Test input");

		assertThrows(IllegalArgumentException.class, () -> {
			embeddingService.createEmbeddings(request);
		});
	}

	@Test
	void testCreateEmbeddingsWithZeroDimensions() {
		EmbeddingCreateParams request = EmbeddingCreateParams.builder()
			.model(Constants.ModelEmbedding3)
			.dimensions(0)
			.build();
		request.setInput("Test input");

		assertThrows(IllegalArgumentException.class, () -> {
			embeddingService.createEmbeddings(request);
		});
	}

}
