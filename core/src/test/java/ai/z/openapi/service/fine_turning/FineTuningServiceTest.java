package ai.z.openapi.service.fine_turning;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FineTuningService test class for testing various functionalities of FineTuningService
 * and FineTuningServiceImpl
 */
@DisplayName("FineTuningService Tests")
public class FineTuningServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(FineTuningServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private FineTuningService fineTuningService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "fine-tuning-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		fineTuningService = client.fineTuning();
	}

	@Test
	@DisplayName("Test FineTuningService Instantiation")
	void testFineTuningServiceInstantiation() {
		assertNotNull(fineTuningService, "FineTuningService should be properly instantiated");
		assertInstanceOf(FineTuningServiceImpl.class, fineTuningService,
				"FineTuningService should be an instance of FineTuningServiceImpl");
	}

	@Test
	@DisplayName("Test Create Fine-Tuning Job")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateFineTuningJob() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FineTuningJobRequest request = FineTuningJobRequest.builder()
			.requestId(requestId)
			.model("chatglm3-6b")
			.training_file("file-test-training-data")
			.build();

		// Execute test
		CreateFineTuningJobApiResponse response = fineTuningService.createFineTuningJob(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Create fine-tuning job response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Retrieve Fine-Tuning Job")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveFineTuningJob() throws JsonProcessingException {
		// Prepare test data
		QueryFineTuningJobRequest request = QueryFineTuningJobRequest.builder().jobId("ftjob-test-job-id").build();

		// Execute test
		QueryFineTuningJobApiResponse response = fineTuningService.retrieveFineTuningJob(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Retrieve fine-tuning job response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Fine-Tuning Job Events")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListFineTuningJobEvents() throws JsonProcessingException {
		// Prepare test data
		QueryFineTuningJobRequest request = QueryFineTuningJobRequest.builder()
			.jobId("ftjob-test-job-id")
			.limit(10)
			.build();

		// Execute test
		QueryFineTuningEventApiResponse response = fineTuningService.listFineTuningJobEvents(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("List fine-tuning job events response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Personal Fine-Tuning Jobs")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListPersonalFineTuningJobs() throws JsonProcessingException {
		// Prepare test data
		QueryPersonalFineTuningJobRequest request = QueryPersonalFineTuningJobRequest.builder().limit(5).build();

		// Execute test
		QueryPersonalFineTuningJobApiResponse response = fineTuningService.listPersonalFineTuningJobs(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("List personal fine-tuning jobs response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Cancel Fine-Tuning Job")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCancelFineTuningJob() throws JsonProcessingException {
		// Prepare test data
		FineTuningJobIdRequest request = FineTuningJobIdRequest.builder().jobId("ftjob-test-job-id").build();

		// Execute test
		QueryFineTuningJobApiResponse response = fineTuningService.cancelFineTuningJob(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Cancel fine-tuning job response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Delete Fine-Tuning Job")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDeleteFineTuningJob() throws JsonProcessingException {
		// Prepare test data
		FineTuningJobIdRequest request = FineTuningJobIdRequest.builder().jobId("ftjob-test-job-id").build();

		// Execute test
		QueryFineTuningJobApiResponse response = fineTuningService.deleteFineTuningJob(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Delete fine-tuning job response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Delete Fine-Tuned Model")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDeleteFineTunedModel() throws JsonProcessingException {
		// Prepare test data
		FineTuningJobModelRequest request = FineTuningJobModelRequest.builder()
			.fineTunedModel("test-fine-tuned-model")
			.build();

		// Execute test
		FineTunedModelsStatusResponse response = fineTuningService.deleteFineTunedModel(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Delete fine-tuned model response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Create Job")
	void testValidation_NullCreateJobRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			fineTuningService.createFineTuningJob(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Retrieve Job")
	void testValidation_NullRetrieveJobRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			fineTuningService.retrieveFineTuningJob(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for List Events")
	void testValidation_NullListEventsRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			fineTuningService.listFineTuningJobEvents(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for List Personal Jobs")
	void testValidation_NullListPersonalJobsRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			fineTuningService.listPersonalFineTuningJobs(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Cancel Job")
	void testValidation_NullCancelJobRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			fineTuningService.cancelFineTuningJob(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Delete Job")
	void testValidation_NullDeleteJobRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			fineTuningService.deleteFineTuningJob(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request for Delete Model")
	void testValidation_NullDeleteModelRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			fineTuningService.deleteFineTunedModel(null);
		}, "Null request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Create Fine-Tuning Job with Validation File")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateFineTuningJobWithValidationFile() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		FineTuningJobRequest request = FineTuningJobRequest.builder()
			.requestId(requestId)
			.model("chatglm3-6b")
			.training_file("file-test-training-data")
			.validationFile("file-test-validation-data")
			.build();

		// Execute test
		CreateFineTuningJobApiResponse response = fineTuningService.createFineTuningJob(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Create fine-tuning job with validation file response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Fine-Tuning Job Events with Pagination")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListFineTuningJobEventsWithPagination() throws JsonProcessingException {
		// Prepare test data
		QueryFineTuningJobRequest request = QueryFineTuningJobRequest.builder()
			.jobId("ftjob-test-job-id")
			.limit(5)
			.after("event-cursor-id")
			.build();

		// Execute test
		QueryFineTuningEventApiResponse response = fineTuningService.listFineTuningJobEvents(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("List fine-tuning job events with pagination response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Personal Fine-Tuning Jobs with Pagination")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListPersonalFineTuningJobsWithPagination() throws JsonProcessingException {
		// Prepare test data
		QueryPersonalFineTuningJobRequest request = QueryPersonalFineTuningJobRequest.builder()
			.limit(3)
			.after("job-cursor-id")
			.build();

		// Execute test
		QueryPersonalFineTuningJobApiResponse response = fineTuningService.listPersonalFineTuningJobs(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("List personal fine-tuning jobs with pagination response: {}", mapper.writeValueAsString(response));
	}

}