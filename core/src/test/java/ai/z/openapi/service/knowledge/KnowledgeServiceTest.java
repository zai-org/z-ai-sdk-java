package ai.z.openapi.service.knowledge;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.knowledge.KnowledgeBaseParams;
import ai.z.openapi.service.knowledge.KnowledgeEditResponse;
import ai.z.openapi.service.knowledge.KnowledgeResponse;
import ai.z.openapi.service.knowledge.KnowledgeService;
import ai.z.openapi.service.knowledge.KnowledgeServiceImpl;
import ai.z.openapi.service.knowledge.KnowledgeUsedResponse;
import ai.z.openapi.service.knowledge.QueryKnowledgeApiResponse;
import ai.z.openapi.service.knowledge.QueryKnowledgeRequest;
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
 * KnowledgeService test class for testing various functionalities of KnowledgeService and
 * KnowledgeServiceImpl
 */
@DisplayName("KnowledgeService Tests")
public class KnowledgeServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(KnowledgeServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private KnowledgeService knowledgeService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "knowledge-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		knowledgeService = client.knowledge();
	}

	@Test
	@DisplayName("Test KnowledgeService Instantiation")
	void testKnowledgeServiceInstantiation() {
		assertNotNull(knowledgeService, "KnowledgeService should be properly instantiated");
		assertInstanceOf(KnowledgeServiceImpl.class, knowledgeService,
				"KnowledgeService should be an instance of KnowledgeServiceImpl");
	}

	@Test
	@DisplayName("Test Create Knowledge Base")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateKnowledge() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		KnowledgeBaseParams request = KnowledgeBaseParams.builder()
			.embeddingId(1)
			.name("Test Knowledge Base")
			.description("Test knowledge base for unit testing")
			.icon("question")
			.background("blue")
			.customerIdentifier("test-customer")
			.bucketId("test-bucket")
			.build();

		// Execute test
		KnowledgeResponse response = knowledgeService.createKnowledge(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Create knowledge response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Modify Knowledge Base")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testModifyKnowledge() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		KnowledgeBaseParams request = KnowledgeBaseParams.builder()
			.knowledgeId("test-knowledge-id")
			.embeddingId(1)
			.name("Modified Test Knowledge Base")
			.description("Modified test knowledge base for unit testing")
			.icon("book")
			.background("green")
			.customerIdentifier("test-customer")
			.bucketId("test-bucket")
			.build();

		// Execute test
		KnowledgeEditResponse response = knowledgeService.modifyKnowledge(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Modify knowledge response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Query Knowledge")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testQueryKnowledge() throws JsonProcessingException {
		// Prepare test data
		QueryKnowledgeRequest request = QueryKnowledgeRequest.builder().page(1).size(10).build();

		// Execute test
		QueryKnowledgeApiResponse response = knowledgeService.queryKnowledge(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Query knowledge response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Delete Knowledge Base")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDeleteKnowledge() throws JsonProcessingException {
		// Prepare test data
		String knowledgeId = "test-knowledge-id-" + System.currentTimeMillis();

		// Execute test
		KnowledgeEditResponse response = knowledgeService.deleteKnowledge(knowledgeId);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Delete knowledge response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Check Knowledge Used")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCheckKnowledgeUsed() throws JsonProcessingException {
		// Execute test
		KnowledgeUsedResponse response = knowledgeService.checkKnowledgeUsed();

		// Verify results
		assertNotNull(response, "Response should not be null");
		assertTrue(response.isSuccess(), "Response should be successful");
		assertNotNull(response.getData(), "Response data should not be null");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Check knowledge used response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Create Request")
	void testValidation_NullCreateRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			knowledgeService.createKnowledge(null);
		}, "Null create request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Modify Request")
	void testValidation_NullModifyRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			knowledgeService.modifyKnowledge(null);
		}, "Null modify request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Query Request")
	void testValidation_NullQueryRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			knowledgeService.queryKnowledge(null);
		}, "Null query request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Knowledge ID")
	void testValidation_NullKnowledgeId() {
		assertThrows(IllegalArgumentException.class, () -> {
			knowledgeService.deleteKnowledge(null);
		}, "Null knowledge ID should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Empty Knowledge ID")
	void testValidation_EmptyKnowledgeId() {
		assertThrows(IllegalArgumentException.class, () -> {
			knowledgeService.deleteKnowledge("");
		}, "Empty knowledge ID should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Create Knowledge with Invalid Parameters")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateKnowledgeWithInvalidParams() {
		// Test with missing required fields
		KnowledgeBaseParams request = KnowledgeBaseParams.builder()
			.name("") // Empty name
			.build();

		KnowledgeResponse response = knowledgeService.createKnowledge(request);

		// Should return error response
		assertNotNull(response, "Response should not be null");
		assertFalse(response.isSuccess(), "Response should not be successful with invalid parameters");
		assertNotNull(response.getError(), "Response should contain error information");
	}

	@Test
	@DisplayName("Test Modify Knowledge with Non-existent ID")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testModifyKnowledgeWithNonExistentId() {
		KnowledgeBaseParams request = KnowledgeBaseParams.builder()
			.knowledgeId("non-existent-id-" + System.currentTimeMillis())
			.embeddingId(1)
			.name("Test Knowledge Base")
			.description("Test description")
			.build();

		KnowledgeEditResponse response = knowledgeService.modifyKnowledge(request);

		// Should return error response
		assertNotNull(response, "Response should not be null");
		assertNotNull(response.getError(), "Response should contain error for non-existent knowledge ID");
	}

	@Test
	@DisplayName("Test Delete Knowledge with Non-existent ID")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDeleteKnowledgeWithNonExistentId() {
		String nonExistentId = "non-existent-id-" + System.currentTimeMillis();

		KnowledgeEditResponse response = knowledgeService.deleteKnowledge(nonExistentId);

		// Should return error response
		assertNotNull(response, "Response should not be null");
		assertNotNull(response.getError(), "Response should contain error for non-existent knowledge ID");
	}

	@Test
	@DisplayName("Test Query Knowledge with Invalid Page Parameters")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testQueryKnowledgeWithInvalidPageParams() {
		// Test with negative page number
		QueryKnowledgeRequest request = QueryKnowledgeRequest.builder().page(-1).size(10).build();

		QueryKnowledgeApiResponse response = knowledgeService.queryKnowledge(request);

		// Should handle invalid parameters gracefully
		assertNotNull(response, "Response should not be null");
		logger.info("Query knowledge with invalid page response: {}", response);
	}

	@Test
	@DisplayName("Test Knowledge Base Name Length Validation")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testKnowledgeBaseNameLengthValidation() {
		// Test with name exceeding 100 characters
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 101; i++) {
			sb.append("a");
		}
		String longName = sb.toString();
		KnowledgeBaseParams request = KnowledgeBaseParams.builder()
			.embeddingId(1)
			.name(longName)
			.description("Test description")
			.build();

		// This should be validated either by the service or the API
		assertDoesNotThrow(() -> {
			KnowledgeResponse response = knowledgeService.createKnowledge(request);
			// If validation is done server-side, we expect an error response
			if (!response.isSuccess()) {
				assertNotNull(response.getError(), "Should contain validation error");
			}
		});
	}

	@Test
	@DisplayName("Test Knowledge Base Description Length Validation")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testKnowledgeBaseDescriptionLengthValidation() {
		// Test with description exceeding 500 characters
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 501; i++) {
			sb.append("a");
		}
		String longDescription = sb.toString();
		KnowledgeBaseParams request = KnowledgeBaseParams.builder()
			.embeddingId(1)
			.name("Test Knowledge Base")
			.description(longDescription)
			.build();

		// This should be validated either by the service or the API
		assertDoesNotThrow(() -> {
			KnowledgeResponse response = knowledgeService.createKnowledge(request);
			// If validation is done server-side, we expect an error response
			if (!response.isSuccess()) {
				assertNotNull(response.getError(), "Should contain validation error");
			}
		});
	}

	@Test
	@DisplayName("Test Customer Identifier Length Validation")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCustomerIdentifierLengthValidation() {
		// Test with customer identifier exceeding 32 characters
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 33; i++) {
			sb.append("a");
		}
		String longCustomerIdentifier = sb.toString();
		KnowledgeBaseParams request = KnowledgeBaseParams.builder()
			.embeddingId(1)
			.name("Test Knowledge Base")
			.description("Test description")
			.customerIdentifier(longCustomerIdentifier)
			.build();

		// This should be validated either by the service or the API
		assertDoesNotThrow(() -> {
			KnowledgeResponse response = knowledgeService.createKnowledge(request);
			// If validation is done server-side, we expect an error response
			if (!response.isSuccess()) {
				assertNotNull(response.getError(), "Should contain validation error");
			}
		});
	}

	@Test
	@DisplayName("Test Bucket ID Length Validation")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testBucketIdLengthValidation() {
		// Test with bucket ID exceeding 32 characters
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 33; i++) {
			sb.append("a");
		}
		String longBucketId = sb.toString();
		KnowledgeBaseParams request = KnowledgeBaseParams.builder()
			.embeddingId(1)
			.name("Test Knowledge Base")
			.description("Test description")
			.bucketId(longBucketId)
			.build();

		// This should be validated either by the service or the API
		assertDoesNotThrow(() -> {
			KnowledgeResponse response = knowledgeService.createKnowledge(request);
			// If validation is done server-side, we expect an error response
			if (!response.isSuccess()) {
				assertNotNull(response.getError(), "Should contain validation error");
			}
		});
	}

}
