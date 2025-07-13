package ai.z.openapi.service.batches;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.file.QueryBatchRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BatchService test class for testing various functionalities of BatchService and
 * BatchServiceImpl
 */
@DisplayName("BatchService Tests")
public class BatchServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(BatchServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private BatchService batchService;

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		batchService = client.batches();
	}

	@Test
	@DisplayName("Test BatchService Instantiation")
	void testBatchServiceInstantiation() {
		assertNotNull(batchService, "BatchService should be properly instantiated");
		assertInstanceOf(BatchServiceImpl.class, batchService,
				"BatchService should be an instance of BatchServiceImpl");
	}

	@Test
	@DisplayName("Test Create Batch")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateBatch() throws JsonProcessingException {
		// Prepare test data
		Map<String, String> metadata = new HashMap<>();
		metadata.put("description", "Test batch creation");
		metadata.put("project", "batch-service-test");

		BatchCreateParams batchCreateParams = BatchCreateParams.builder()
			.completionWindow("24h")
			.endpoint("/v1/chat/completions")
			.inputFileId("file-test-123")
			.metadata(metadata)
			.build();

		// Execute test
		BatchResponse response = batchService.createBatch(batchCreateParams);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Create batch response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Retrieve Batch by Request")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveBatchByRequest() throws JsonProcessingException {
		// Prepare test data
		BatchRequest batchRequest = BatchRequest.builder().batchId("batch-test-123").build();

		// Execute test
		BatchResponse response = batchService.retrieveBatch(batchRequest);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Retrieve batch by request response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Retrieve Batch by ID")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveBatchById() throws JsonProcessingException {
		// Execute test
		BatchResponse response = batchService.retrieveBatch("batch-test-123");

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Retrieve batch by ID response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Batches")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListBatches() throws JsonProcessingException {
		// Prepare test data
		QueryBatchRequest queryBatchRequest = QueryBatchRequest.builder().limit(10).after("batch-123").build();

		// Execute test
		QueryBatchResponse response = batchService.listBatches(queryBatchRequest);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("List batches response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Cancel Batch by Request")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCancelBatchByRequest() throws JsonProcessingException {
		// Prepare test data
		BatchRequest batchRequest = BatchRequest.builder().batchId("batch-test-123").build();

		// Execute test
		BatchResponse response = batchService.cancelBatch(batchRequest);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Cancel batch by request response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Cancel Batch by ID")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCancelBatchById() throws JsonProcessingException {
		// Execute test
		BatchResponse response = batchService.cancelBatch("batch-test-123");

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Cancel batch by ID response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null BatchCreateParams")
	void testValidation_NullBatchCreateParams() {
		assertThrows(IllegalArgumentException.class, () -> {
			batchService.createBatch(null);
		}, "Null BatchCreateParams should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null BatchRequest")
	void testValidation_NullBatchRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			batchService.retrieveBatch((BatchRequest) null);
		}, "Null BatchRequest should throw IllegalArgumentException");

		assertThrows(IllegalArgumentException.class, () -> {
			batchService.cancelBatch((BatchRequest) null);
		}, "Null BatchRequest should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Batch ID")
	void testValidation_NullBatchId() {
		assertThrows(IllegalArgumentException.class, () -> {
			batchService.retrieveBatch((String) null);
		}, "Null batch ID should throw IllegalArgumentException");

		assertThrows(IllegalArgumentException.class, () -> {
			batchService.cancelBatch((String) null);
		}, "Null batch ID should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Empty Batch ID")
	void testValidation_EmptyBatchId() {
		assertThrows(IllegalArgumentException.class, () -> {
			batchService.retrieveBatch("");
		}, "Empty batch ID should throw IllegalArgumentException");

		assertThrows(IllegalArgumentException.class, () -> {
			batchService.cancelBatch("");
		}, "Empty batch ID should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null QueryBatchRequest")
	void testValidation_NullQueryBatchRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			batchService.listBatches(null);
		}, "Null QueryBatchRequest should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - BatchCreateParams with Null Fields")
	void testValidation_BatchCreateParamsNullFields() {
		// Test null endpoint
		BatchCreateParams paramsNullEndpoint = BatchCreateParams.builder()
			.completionWindow("24h")
			.inputFileId("file-123")
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			batchService.createBatch(paramsNullEndpoint);
		}, "BatchCreateParams with null endpoint should throw IllegalArgumentException");

		// Test null inputFileId
		BatchCreateParams paramsNullInputFileId = BatchCreateParams.builder()
			.completionWindow("24h")
			.endpoint("/v1/chat/completions")
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			batchService.createBatch(paramsNullInputFileId);
		}, "BatchCreateParams with null inputFileId should throw IllegalArgumentException");

		// Test null completionWindow
		BatchCreateParams paramsNullCompletionWindow = BatchCreateParams.builder()
			.endpoint("/v1/chat/completions")
			.inputFileId("file-123")
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			batchService.createBatch(paramsNullCompletionWindow);
		}, "BatchCreateParams with null completionWindow should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - BatchRequest with Null Batch ID")
	void testValidation_BatchRequestNullBatchId() {
		BatchRequest batchRequestNullId = BatchRequest.builder().build();

		assertThrows(IllegalArgumentException.class, () -> {
			batchService.retrieveBatch(batchRequestNullId);
		}, "BatchRequest with null batchId should throw IllegalArgumentException");

		assertThrows(IllegalArgumentException.class, () -> {
			batchService.cancelBatch(batchRequestNullId);
		}, "BatchRequest with null batchId should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Batch Creation with Metadata")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testBatchCreationWithMetadata() throws JsonProcessingException {
		// Prepare test data with comprehensive metadata
		Map<String, String> metadata = new HashMap<>();
		metadata.put("project", "batch-processing-test");
		metadata.put("environment", "test");
		metadata.put("version", "1.0.0");
		metadata.put("created_by", "batch-service-test");

		BatchCreateParams batchCreateParams = BatchCreateParams.builder()
			.completionWindow("24h")
			.endpoint("/v1/chat/completions")
			.inputFileId("file-comprehensive-test-456")
			.metadata(metadata)
			.build();

		// Execute test
		BatchResponse response = batchService.createBatch(batchCreateParams);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Batch creation with metadata response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Batches with Pagination")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListBatchesWithPagination() throws JsonProcessingException {
		// Test with different pagination parameters
		QueryBatchRequest queryBatchRequest1 = QueryBatchRequest.builder().limit(5).build();

		QueryBatchResponse response1 = batchService.listBatches(queryBatchRequest1);
		assertNotNull(response1, "First page response should not be null");
		logger.info("First page batches response: {}", mapper.writeValueAsString(response1));

		// Test with after parameter
		QueryBatchRequest queryBatchRequest2 = QueryBatchRequest.builder().limit(3).after("batch-456").build();

		QueryBatchResponse response2 = batchService.listBatches(queryBatchRequest2);
		assertNotNull(response2, "Second page response should not be null");
		logger.info("Second page batches response: {}", mapper.writeValueAsString(response2));
	}

}