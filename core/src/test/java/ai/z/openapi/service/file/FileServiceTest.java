package ai.z.openapi.service.file;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.core.response.HttpxBinaryResponseContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileService test class for testing various functionalities of FileService and
 * FileServiceImpl
 */
@DisplayName("FileService Tests")
public class FileServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(FileServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private FileService fileService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "file-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		fileService = client.files();
	}

	@Test
	@DisplayName("Test FileService Instantiation")
	void testFileServiceInstantiation() {
		assertNotNull(fileService, "FileService should be properly instantiated");
		assertInstanceOf(FileServiceImpl.class, fileService, "FileService should be an instance of FileServiceImpl");
	}

	@Test
	@DisplayName("Test File Upload - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testFileUpload() throws JsonProcessingException, IOException {
		// Create a temporary test file
		Path tempFile = createTempTestFile("test-upload.txt", "This is a test file for upload.");

		try {
			String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

			FileUploadParams request = FileUploadParams.builder()
				.filePath(tempFile.toString())
				.purpose(UploadFilePurpose.AGENT.value())
				.requestId(requestId)
				.build();

			// Execute test
			FileApiResponse response = fileService.uploadFile(request);

			// Verify results
			assertNotNull(response, "Response should not be null");
			assertTrue(response.isSuccess(), "Response should be successful");
			assertNotNull(response.getData(), "Response data should not be null");
			assertNotNull(response.getData().getId(), "File ID should not be null");
			assertEquals("file", response.getData().getObject(), "Object type should be 'file'");
			assertEquals(UploadFilePurpose.AGENT.value(), response.getData().getPurpose(), "Purpose should match");
			assertNotNull(response.getData().getFilename(), "Filename should not be null");
			assertNotNull(response.getData().getBytes(), "File size should not be null");
			assertTrue(response.getData().getBytes() > 0, "File size should be greater than 0");
			assertNull(response.getError(), "Response error should be null");

			logger.info("File upload response: {}", mapper.writeValueAsString(response));
		}
		finally {
			// Clean up temporary file
			Files.deleteIfExists(tempFile);
		}
	}

	@Test
	@DisplayName("Test File Upload with Extra JSON Parameters")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testFileUploadWithExtraJson() throws IOException {
		// Create a temporary test file
		Path tempFile = createTempTestFile("test-upload-extra.txt", "This is a test file with extra parameters.");

		try {
			String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

			// Add extra JSON parameters
			Map<String, Object> extraJson = new HashMap<>();
			extraJson.put("description", "Test file with extra parameters");
			extraJson.put("version", 1);
			extraJson.put("isTest", true);

			FileUploadParams request = FileUploadParams.builder()
				.filePath(tempFile.toString())
				.purpose(UploadFilePurpose.FILE_EXTRACT.value())
				.requestId(requestId)
				.extraJson(extraJson)
				.build();

			// Execute test
			FileApiResponse response = fileService.uploadFile(request);

			// Verify results
			assertNotNull(response, "Response should not be null");
			assertTrue(response.isSuccess(), "Response should be successful");
			assertNotNull(response.getData(), "Response data should not be null");
			assertEquals(UploadFilePurpose.FILE_EXTRACT.value(), response.getData().getPurpose(),
					"Purpose should match");
			assertNull(response.getError(), "Response error should be null");

			logger.info("File upload with extra JSON response: {}", mapper.writeValueAsString(response));
		}
		finally {
			// Clean up temporary file
			Files.deleteIfExists(tempFile);
		}
	}

	@Test
	@DisplayName("Test File Upload Error - File Not Found")
	void testFileUploadError_FileNotFound() {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FileUploadParams request = FileUploadParams.builder()
			.filePath("/non/existent/file.txt")
			.purpose(UploadFilePurpose.AGENT.value())
			.requestId(requestId)
			.build();

		// Execute test and expect exception
		assertThrows(RuntimeException.class, () -> {
			fileService.uploadFile(request);
		}, "Should throw RuntimeException for non-existent file");
	}

	@Test
	@DisplayName("Test List Files - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListFiles() throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FileListParams request = FileListParams.builder().limit(10).order("desc").requestId(requestId).build();

		// Execute test
		QueryFileApiResponse response = fileService.listFiles(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNotNull(response.getData().getData(), "File list should not be null");
		assertNull(response.getError(), "Response error should be null");

		logger.info("List files response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Delete File - Basic Functionality")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDeleteFile() throws JsonProcessingException {

		String fileId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		FileDelRequest request = FileDelRequest.builder().fileId(fileId).build();

		// Execute test
		FileDelResponse response = fileService.deleteFile(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertFalse(response.isSuccess(), "Response should be failed");
		assertNull(response.getData(), "Response data should not be null");
		assertNotNull(response.getError(), "Response error should be null");

		logger.info("Delete file response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Files with Purpose Filter")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListFilesWithPurpose() throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FileListParams request = FileListParams.builder()
			.purpose(UploadFilePurpose.AGENT.value())
			.limit(5)
			.order("asc")
			.requestId(requestId)
			.build();

		// Execute test
		QueryFileApiResponse response = fileService.listFiles(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNull(response.getError(), "Response error should be null");

		// Verify purpose filter if files exist
		if (response.getData().getData() != null && !response.getData().getData().isEmpty()) {
			response.getData().getData().forEach(file -> {
				assertEquals(UploadFilePurpose.AGENT.value(), file.getPurpose(),
						"All files should have the specified purpose");
			});
		}

		logger.info("List files with purpose filter response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Files with Pagination")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListFilesWithPagination() throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		// First page
		FileListParams firstPageRequest = FileListParams.builder().limit(2).order("desc").requestId(requestId).build();

		QueryFileApiResponse firstPageResponse = fileService.listFiles(firstPageRequest);

		assertNotNull(firstPageResponse, "First page response should not be null");
		assertTrue(firstPageResponse.isSuccess(), "First page response should be successful");

		// If there are files, test second page
		if (firstPageResponse.getData() != null && firstPageResponse.getData().getData() != null
				&& !firstPageResponse.getData().getData().isEmpty()) {

			// Get the last file ID for pagination
			String lastFileId = firstPageResponse.getData()
				.getData()
				.get(firstPageResponse.getData().getData().size() - 1)
				.getId();

			// Second page
			FileListParams secondPageRequest = FileListParams.builder()
				.limit(2)
				.order("desc")
				.after(lastFileId)
				.requestId(requestId + "-page2")
				.build();

			QueryFileApiResponse secondPageResponse = fileService.listFiles(secondPageRequest);

			assertNotNull(secondPageResponse, "Second page response should not be null");
			assertTrue(secondPageResponse.isSuccess(), "Second page response should be successful");

			logger.info("Pagination test - First page: {}, Second page: {}",
					mapper.writeValueAsString(firstPageResponse), mapper.writeValueAsString(secondPageResponse));
		}
		else {
			logger.info("No files available for pagination test");
		}
	}

	@Test
	@DisplayName("Test Retrieve File Content")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveFileContent() throws IOException {
		// First upload a file to get a valid file ID
		Path tempFile = createTempTestFile("test-content.txt", "This is test content for retrieval.");

		try {
			String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

			FileUploadParams uploadRequest = FileUploadParams.builder()
				.filePath(tempFile.toString())
				.purpose(UploadFilePurpose.FILE_EXTRACT.value())
				.requestId(requestId)
				.build();

			FileApiResponse uploadResponse = fileService.uploadFile(uploadRequest);
			assertTrue(uploadResponse.isSuccess(), "File upload should be successful");

			String fileId = uploadResponse.getData().getId();

			// Retrieve file content
			HttpxBinaryResponseContent content = fileService.retrieveFileContent(fileId);

			// Verify results
			assertNotNull(content, "File content should not be null");

			// Verify we can read content
			byte[] contentBytes = content.getContent();
			assertNotNull(contentBytes, "Content bytes should not be null");
			assertTrue(contentBytes.length > 0, "Content should not be empty");

			logger.info("File content retrieved successfully for file ID: {}", fileId);
		}
		finally {
			// Clean up temporary file
			Files.deleteIfExists(tempFile);
		}
	}

	@Test
	@DisplayName("Test Retrieve File Content Error - Invalid File ID")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveFileContentError_InvalidFileId() {
		String invalidFileId = "invalid-file-id-" + System.currentTimeMillis();

		// Execute test and expect IOException
		assertThrows(IOException.class, () -> {
			fileService.retrieveFileContent(invalidFileId);
		}, "Should throw IOException for invalid file ID");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Upload Request")
	void testValidation_NullUploadRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			fileService.uploadFile(null);
		}, "Null upload request should throw NullPointerException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null List Request")
	void testValidation_NullListRequest() {
		assertThrows(NullPointerException.class, () -> {
			fileService.listFiles(null);
		}, "Null list request should throw NullPointerException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null File ID")
	void testValidation_NullFileId() {
		assertThrows(Exception.class, () -> {
			fileService.retrieveFileContent(null);
		}, "Null file ID should throw Exception");
	}

	/**
	 * Helper method to create a temporary test file
	 */
	private Path createTempTestFile(String filename, String content) throws IOException {
		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
		Path tempFile = tempDir.resolve(filename);
		Files.write(tempFile, content.getBytes());
		return tempFile;
	}

}
