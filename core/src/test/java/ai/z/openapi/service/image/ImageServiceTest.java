package ai.z.openapi.service.image;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.core.config.ZaiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * ImageService test class for testing various functionalities of ImageService and
 * ImageServiceImpl
 */
@DisplayName("ImageService Tests")
public class ImageServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(ImageServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private ImageService imageService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "image-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		imageService = client.images();
	}

	@Test
	@DisplayName("Test ImageService Instantiation")
	void testImageServiceInstantiation() {
		assertNotNull(imageService, "ImageService should be properly instantiated");
		assertInstanceOf(ImageServiceImpl.class, imageService,
				"ImageService should be an instance of ImageServiceImpl");
	}

	@Test
	@DisplayName("Test Image Generation - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateImage() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		CreateImageRequest request = CreateImageRequest.builder()
			.model(Constants.ModelCogView3Plus)
			.prompt("A beautiful sunset over mountains")
			.size("1024x1024")
			.requestId(requestId)
			.build();

		// Execute test
		ImageResponse response = imageService.createImage(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNotNull(response.getData().getData(), "Image data should not be null");
		assertFalse(response.getData().getData().isEmpty(), "Image data should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Image generation response: {}", mapper.writeValueAsString(response));
	}

	@ParameterizedTest
	@ValueSource(strings = { Constants.ModelCogView3Plus, Constants.ModelCogView3 })
	@DisplayName("Test Different Image Models")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDifferentImageModels(String model) throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		CreateImageRequest request = CreateImageRequest.builder()
			.model(model)
			.prompt("A serene landscape with trees and water")
			.size("512x512")
			.requestId(requestId)
			.build();

		ImageResponse response = imageService.createImage(request);

		assertNotNull(response, "Response should not be null");
		logger.info("Model {} response: {}", model, mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request")
	void testValidation_NullRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			imageService.createImage(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Different Image Sizes")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDifferentImageSizes() throws JsonProcessingException {
		String[] sizes = { "512x512", "1024x1024" };

		for (String size : sizes) {
			String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

			CreateImageRequest request = CreateImageRequest.builder()
				.model(Constants.ModelCogView4)
				.prompt("A simple geometric pattern")
				.size(size)
				.requestId(requestId)
				.build();

			ImageResponse response = imageService.createImage(request);

			assertNotNull(response.getData(), "Response should not be null for size: " + size);
			assertEquals(200, response.getCode());
			logger.info("Size {} response: {}", size, mapper.writeValueAsString(response));
		}
	}

	@Test
	@DisplayName("Test Complex Prompt")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testComplexPrompt() throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		CreateImageRequest request = CreateImageRequest.builder()
			.model(Constants.ModelCogView3Plus)
			.prompt("A futuristic cityscape at night with neon lights, flying cars, and towering skyscrapers reflecting in a calm river, digital art style")
			.size("1024x1024")
			.requestId(requestId)
			.build();

		ImageResponse response = imageService.createImage(request);

		assertNotNull(response, "Response should not be null");
		logger.info("Complex prompt response: {}", mapper.writeValueAsString(response));
	}

}
