package ai.z.openapi.service.fileparsing;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
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
 * FileParsingService test class for testing various functionalities of FileParsingService
 * and FileParsingServiceImpl
 */
@DisplayName("FileParsingService Tests")
public class FileParsingServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(FileParsingServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private FileParsingService fileParsingService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "fileparsing-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		fileParsingService = client.fileParsing(); // 假设你的 client 有 fileParsing() 方法
	}

	@Test
	@DisplayName("Test FileParsingService Instantiation")
	void testFileParsingServiceInstantiation() {
		assertNotNull(fileParsingService, "FileParsingService should be properly instantiated");
		assertInstanceOf(FileParsingServiceImpl.class, fileParsingService,
				"FileParsingService should be an instance of FileParsingServiceImpl");
	}

	@Test
	@DisplayName("Test File Parsing Task Creation - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateParseTask() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FileParsingUploadReq request = FileParsingUploadReq.builder()
			.toolType("excel")
			.fileType("xlsx")
			.filePath("src/test/resources/test.xlsx") // 确保有测试文件
			.build();

		// Execute test
		FileParsingResponse response = fileParsingService.createParseTask(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertEquals(200, response.getCode());
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNotNull(response.getData().getTaskId(), "Response data taskId should not be null");
		assertNull(response.getError(), "Response error should be null");
		logger.info("File parsing task create response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test File Parsing Result Retrieval")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testGetParseResult() throws JsonProcessingException {
		// First create a file parsing task
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FileParsingUploadReq uploadRequest = FileParsingUploadReq.builder()
			.toolType("excel")
			.fileType("xlsx")
			.filePath("src/test/resources/test.xlsx")
			.build();

		FileParsingResponse createResp = fileParsingService.createParseTask(uploadRequest);

		assertNotNull(createResp, "Create response should not be null");
		assertTrue(createResp.isSuccess(), "Create response should be successful");
		assertNotNull(createResp.getData(), "Create response data should not be null");
		assertNotNull(createResp.getData().getTaskId(), "Task ID should not be null");

		// Retrieve the result using task ID
		String taskId = createResp.getData().getTaskId();
		FileParsingDownloadReq downloadReq = FileParsingDownloadReq.builder().taskId(taskId).formatType("json").build();

		FileParsingDownloadResponse downloadResp = fileParsingService.getParseResult(downloadReq);

		assertNotNull(downloadResp, "Download response should not be null");
		assertEquals(200, downloadResp.getCode());
		assertNotNull(downloadResp.getData(), "Download response data should not be null");
		logger.info("File parsing result: taskId={}, response={}", taskId, mapper.writeValueAsString(downloadResp));
	}

	@Test
	@DisplayName("Test File Parsing Result Error")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testGetParseResultError() {
		// Use a mock task ID that doesn't exist
		String mockTaskId = "mock-task-id-" + System.currentTimeMillis();

		FileParsingDownloadReq downloadReq = FileParsingDownloadReq.builder()
			.taskId(mockTaskId)
			.formatType("json")
			.build();

		FileParsingDownloadResponse response = fileParsingService.getParseResult(downloadReq);

		assertNotNull(response, "Response should not be null");
		// For non-existent task, we expect either an error or unsuccessful response
		if (!response.isSuccess()) {
			assertNotNull(response.getError(), "Error should be present for non-existent task");
		}
		logger.info("File parsing result error test: taskId={}, response={}", mockTaskId, response);
	}

	@ParameterizedTest
	@ValueSource(strings = { "excel", "csv", "pdf" })
	@DisplayName("Test Different File Tool Types")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDifferentToolTypes(String toolType) throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FileParsingUploadReq request = FileParsingUploadReq.builder()
			.toolType(toolType)
			.fileType("xlsx")
			.filePath("src/test/resources/test.xlsx")
			.build();

		FileParsingResponse response = fileParsingService.createParseTask(request);

		assertNotNull(response, "Response should not be null");
		assertEquals(200, response.getCode());
		logger.info("ToolType {} response: {}", toolType, mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Create")
	void testValidation_NullRequest_Create() {
		assertThrows(IllegalArgumentException.class, () -> {
			fileParsingService.createParseTask(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null FilePath")
	void testValidation_NullFilePath() {
		FileParsingUploadReq request = FileParsingUploadReq.builder().toolType("excel").fileType("xlsx").build();
		assertThrows(IllegalArgumentException.class, () -> {
			fileParsingService.createParseTask(request);
		}, "Null file path should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null ToolType")
	void testValidation_NullToolType() {
		FileParsingUploadReq request = FileParsingUploadReq.builder()
			.fileType("xlsx")
			.filePath("src/test/resources/test.xlsx")
			.build();
		assertThrows(IllegalArgumentException.class, () -> {
			fileParsingService.createParseTask(request);
		}, "Null toolType should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Download")
	void testValidation_NullRequest_Download() {
		assertThrows(IllegalArgumentException.class, () -> {
			fileParsingService.getParseResult(null);
		}, "Null download request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Task ID Download")
	void testValidation_NullTaskId_Download() {
		FileParsingDownloadReq downloadReq = FileParsingDownloadReq.builder().formatType("json").build();
		assertThrows(IllegalArgumentException.class, () -> {
			fileParsingService.getParseResult(downloadReq);
		}, "Null taskId should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null FormatType Download")
	void testValidation_NullFormatType_Download() {
		FileParsingDownloadReq downloadReq = FileParsingDownloadReq.builder().taskId("test-task-id").build();
		assertThrows(IllegalArgumentException.class, () -> {
			fileParsingService.getParseResult(downloadReq);
		}, "Null formatType should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test File Parsing with Image Input")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateParseTaskWithImage() throws IOException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		String file = ClassLoader.getSystemResource("image_file.png").getFile();
		byte[] bytes = Files.readAllBytes(new File(file).toPath());
		Base64.Encoder encoder = Base64.getEncoder();
		String imageBase64 = encoder.encodeToString(bytes);
		// 假设解析接口支持 imageBase64 字段
		FileParsingUploadReq request = FileParsingUploadReq.builder()
			.toolType("image")
			.fileType("png")
			.filePath(file) // 或文件路径
			.build();

		FileParsingResponse response = fileParsingService.createParseTask(request);

		assertNotNull(response, "Response should not be null");
		assertEquals(200, response.getCode());
		logger.info("File parsing with image input response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test File Parsing Create Task with Custom Settings")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateParseTaskWithCustomSettings() throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FileParsingUploadReq request = FileParsingUploadReq.builder()
			.toolType("excel")
			.fileType("xlsx")
			.filePath("src/test/resources/test.xlsx")
			.build();

		FileParsingResponse response = fileParsingService.createParseTask(request);

		assertNotNull(response, "Response should not be null");
		assertEquals(200, response.getCode());
		logger.info("File parsing with custom settings response: {}", mapper.writeValueAsString(response));
	}

}