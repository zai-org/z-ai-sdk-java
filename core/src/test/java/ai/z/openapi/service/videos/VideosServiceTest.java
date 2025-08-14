package ai.z.openapi.service.videos;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.core.config.ZaiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VideosService test class for testing various functionalities of VideosService and
 * VideosServiceImpl
 */
@DisplayName("VideosService Tests")
public class VideosServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(VideosServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private VideosService videosService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "video-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		videosService = client.videos();
	}

	@Test
	@DisplayName("Test VideosService Instantiation")
	void testVideosServiceInstantiation() {
		assertNotNull(videosService, "VideosService should be properly instantiated");
		assertInstanceOf(VideosServiceImpl.class, videosService,
				"VideosService should be an instance of VideosServiceImpl");
	}

	@Test
	@DisplayName("Test Video Generation - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testVideoGeneration() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		VideoCreateParams request = VideoCreateParams.builder()
			.model(Constants.ModelCogVideoX3)
			.prompt("A beautiful sunset over the ocean with waves gently crashing on the shore")
			.requestId(requestId)
			.withAudio(Boolean.TRUE)
			.quality("speed")
			.duration(5)
			.build();

		// Execute test
		VideosResponse response = videosService.videoGenerations(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertEquals(200, response.getCode());
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNotNull(response.getData().getId(), "Response data ID should not be null");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Video generation response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Video Generation Result Retrieval")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testVideoGenerationResult() throws JsonProcessingException {
		// First create a video generation request
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		VideoCreateParams request = VideoCreateParams.builder()
			.model(Constants.ModelCogVideoX3)
			.prompt("A person walking in a beautiful garden")
			.requestId(requestId)
			.build();

		VideosResponse createResponse = videosService.videoGenerations(request);

		// Verify creation response
		assertNotNull(createResponse, "Create response should not be null");
		assertTrue(createResponse.isSuccess(), "Create response should be successful");
		assertNotNull(createResponse.getData(), "Create response data should not be null");
		assertNotNull(createResponse.getData().getId(), "Task ID should not be null");

		// Retrieve the result using task ID
		String taskId = createResponse.getData().getId();
		VideosResponse resultResponse = videosService.videoGenerationsResult(taskId);

		// Verify result response
		assertNotNull(resultResponse, "Result response should not be null");
		assertEquals(200, resultResponse.getCode());
		assertNotNull(resultResponse.getData(), "Result response data should not be null");
		assertNotNull(resultResponse.getData().getId(), "Result response task ID should not be null");
		logger.info("Video generation result: taskId={}, response={}", taskId,
				mapper.writeValueAsString(resultResponse));
	}

	@Test
	@DisplayName("Test Video Generation Result Error")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testVideoGenerationResultError() {
		// Use a mock task ID that doesn't exist
		String mockTaskId = "mock-task-id-" + System.currentTimeMillis();

		VideosResponse response = videosService.videoGenerationsResult(mockTaskId);

		assertNotNull(response, "Response should not be null");
		// For non-existent task, we expect either an error or unsuccessful response
		if (!response.isSuccess()) {
			assertNotNull(response.getError(), "Error should be present for non-existent task");
		}
		logger.info("Video generation result error test: taskId={}, response={}", mockTaskId, response);
	}

	@ParameterizedTest
	@ValueSource(strings = { Constants.ModelCogVideoX2, Constants.ModelCogVideoX3 })
	@DisplayName("Test Different Video Models")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDifferentModels(String model) throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		VideoCreateParams request = VideoCreateParams.builder()
			.model(model)
			.prompt("A cat playing with a ball of yarn")
			.requestId(requestId)
			.build();

		VideosResponse response = videosService.videoGenerations(request);

		assertNotNull(response, "Response should not be null");
		assertEquals(200, response.getCode());
		logger.info("Model {} response: {}", model, mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request")
	void testValidation_NullRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			videosService.videoGenerations(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Model")
	void testValidation_NullModel() {
		VideoCreateParams request = VideoCreateParams.builder().prompt("Test video prompt").build();

		assertThrows(IllegalArgumentException.class, () -> {
			videosService.videoGenerations(request);
		}, "Null model should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Empty Prompt")
	void testValidation_EmptyPrompt() {
		VideoCreateParams request = VideoCreateParams.builder().model(Constants.ModelCogVideoX3).prompt("").build();

		assertThrows(IllegalArgumentException.class, () -> {
			videosService.videoGenerations(request);
		}, "Empty prompt should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Task ID")
	void testValidation_NullTaskId() {
		assertThrows(IllegalArgumentException.class, () -> {
			videosService.videoGenerationsResult(null);
		}, "Null task ID should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Video Generation with Image Input")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testVideoGenerationWithImage() throws IOException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		String file = ClassLoader.getSystemResource("image_to_video.png").getFile();
		byte[] bytes = Files.readAllBytes(new File(file).toPath());
		Base64.Encoder encoder = Base64.getEncoder();
		String imageUrl = encoder.encodeToString(bytes);
		VideoCreateParams request = VideoCreateParams.builder()
			.model(Constants.ModelCogVideoX3)
			.prompt("Transform this image into a dynamic video scene")
			.imageUrl(imageUrl)
			.requestId(requestId)
			.withAudio(Boolean.FALSE)
			.duration(5)
			.build();

		VideosResponse response = videosService.videoGenerations(request);

		assertNotNull(response, "Response should not be null");
		assertEquals(200, response.getCode());
		logger.info("Video generation with image response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Video Generation with Custom Settings")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testVideoGenerationWithCustomSettings() throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		VideoCreateParams request = VideoCreateParams.builder()
			.model(Constants.ModelCogVideoX3)
			.prompt("A futuristic city with flying cars and neon lights")
			.requestId(requestId)
			.quality("speed")
			.withAudio(Boolean.TRUE)
			.size("1280x720")
			.duration(5)
			.fps(30)
			.build();

		VideosResponse response = videosService.videoGenerations(request);

		assertNotNull(response, "Response should not be null");
		assertEquals(200, response.getCode());
		logger.info("Video generation with custom settings response: {}", mapper.writeValueAsString(response));
	}

}
