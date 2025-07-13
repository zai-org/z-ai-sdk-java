package ai.z.openapi.service.document;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.knowledge.document.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DocumentService test class for testing various functionalities of DocumentService and
 * DocumentServiceImpl
 */
@DisplayName("DocumentService Tests")
public class DocumentServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private DocumentService documentService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "document-test-%d";

	// Test knowledge base ID (should be replaced with actual ID in real tests)
	private static final String TEST_KNOWLEDGE_ID = "test-knowledge-id";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		documentService = client.documents();
	}

	@Test
	@DisplayName("Test DocumentService Instantiation")
	void testDocumentServiceInstantiation() {
		assertNotNull(documentService, "DocumentService should be properly instantiated");
		assertInstanceOf(DocumentServiceImpl.class, documentService,
				"DocumentService should be an instance of DocumentServiceImpl");
	}

	@Test
	@DisplayName("Test Create Document with File Path")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateDocumentWithFilePath() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		DocumentCreateParams request = DocumentCreateParams.builder()
			.filePath("/path/to/test/document.pdf")
			.purpose("retrieval")
			.knowledgeId(TEST_KNOWLEDGE_ID)
			.sentenceSize(200)
			.customSeparator(Arrays.asList("\n", "。"))
			.requestId(requestId)
			.build();

		// Note: This test will fail in real execution due to file not existing
		// It's mainly for testing the parameter validation and structure
		try {
			DocumentObjectResponse response = documentService.createDocument(request);
			// If we reach here, verify the response structure
			assertNotNull(response, "Response should not be null");
			logger.info("Document creation response: {}", mapper.writeValueAsString(response));
		}
		catch (RuntimeException e) {
			// Expected for non-existent file
			assertTrue(e.getMessage().contains("file not found") || e.getMessage().contains("error"));
			logger.info("Expected error for non-existent file: {}", e.getMessage());
		}
	}

	@Test
	@DisplayName("Test Create Document with Upload Detail")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateDocumentWithUploadDetail() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		UploadDetail uploadDetail = new UploadDetail();
		uploadDetail.setUrl("https://example.com/test-document.pdf");
		uploadDetail.setKnowledgeType(1);
		uploadDetail.setFileName("test-document.pdf");
		uploadDetail.setSentenceSize(200);

		DocumentCreateParams request = DocumentCreateParams.builder()
			.uploadDetail(Collections.singletonList(uploadDetail))
			.purpose("retrieval")
			.knowledgeId(TEST_KNOWLEDGE_ID)
			.sentenceSize(200)
			.requestId(requestId)
			.build();

		DocumentObjectResponse response = documentService.createDocument(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Document creation with upload detail response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test List Documents")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListDocuments() throws JsonProcessingException {
		// Prepare test data
		QueryDocumentRequest request = QueryDocumentRequest.builder()
			.knowledgeId(TEST_KNOWLEDGE_ID)
			.purpose("retrieval")
			.page(1)
			.limit(10)
			.order("desc")
			.build();

		QueryDocumentApiResponse response = documentService.listDocuments(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("List documents response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Retrieve Document")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testRetrieveDocument() throws JsonProcessingException {
		// Use a test document ID
		String testDocumentId = "test-document-id-" + System.currentTimeMillis();

		DocumentDataResponse response = documentService.retrieveDocument(testDocumentId);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Retrieve document response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Modify Document")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testModifyDocument() throws JsonProcessingException {
		// Prepare test data
		String testDocumentId = "test-document-id-" + System.currentTimeMillis();

		DocumentEditParams request = DocumentEditParams.builder().id(testDocumentId).knowledgeType(1).build();

		DocumentEditResponse response = documentService.modifyDocument(request);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Modify document response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Delete Document")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDeleteDocument() throws JsonProcessingException {
		// Use a test document ID
		String testDocumentId = "test-document-id-" + System.currentTimeMillis();

		DocumentEditResponse response = documentService.deleteDocument(testDocumentId);

		// Verify results
		assertNotNull(response, "Response should not be null");
		logger.info("Delete document response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Request")
	void testValidation_NullRequest() {
		assertThrows(NullPointerException.class, () -> {
			documentService.createDocument(null);
		}, "Null request should throw NullPointerException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Both FilePath and UploadDetail")
	void testValidation_BothFilePathAndUploadDetail() {
		UploadDetail uploadDetail = new UploadDetail();
		uploadDetail.setUrl("https://example.com/test.pdf");

		DocumentCreateParams request = DocumentCreateParams.builder()
			.filePath("/path/to/file.pdf")
			.uploadDetail(Collections.singletonList(uploadDetail))
			.purpose("retrieval")
			.knowledgeId(TEST_KNOWLEDGE_ID)
			.build();

		assertThrows(RuntimeException.class, () -> {
			documentService.createDocument(request);
		}, "Having both filePath and uploadDetail should throw RuntimeException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Empty Knowledge ID")
	void testValidation_EmptyKnowledgeId() {
		DocumentCreateParams request = DocumentCreateParams.builder()
			.filePath("/path/to/file.pdf")
			.purpose("retrieval")
			.knowledgeId("")
			.build();

		// This should be handled by the API validation
		try {
			DocumentObjectResponse response = documentService.createDocument(request);
			// If response is received, it should contain error information
			if (response != null && !response.isSuccess()) {
				assertNotNull(response.getError(), "Error should be present for invalid knowledge ID");
			}
		}
		catch (Exception e) {
			// Expected for invalid parameters
			logger.info("Expected error for empty knowledge ID: {}", e.getMessage());
		}
	}

	@Test
	@DisplayName("Test Different Document Purposes")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDifferentDocumentPurposes() throws JsonProcessingException {
		String[] purposes = { "retrieval", "fine-tune", "batch" };

		for (String purpose : purposes) {
			String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

			UploadDetail uploadDetail = new UploadDetail();
			uploadDetail.setUrl("https://example.com/test-" + purpose + ".pdf");
			uploadDetail.setKnowledgeType(1);

			DocumentCreateParams request = DocumentCreateParams.builder()
				.uploadDetail(Collections.singletonList(uploadDetail))
				.purpose(purpose)
				.knowledgeId(TEST_KNOWLEDGE_ID)
				.sentenceSize(200)
				.requestId(requestId)
				.build();

			DocumentObjectResponse response = documentService.createDocument(request);

			assertNotNull(response, "Response should not be null for purpose: " + purpose);
			logger.info("Purpose {} response: {}", purpose, mapper.writeValueAsString(response));
		}
	}

	@Test
	@DisplayName("Test Document with Custom Separators")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDocumentWithCustomSeparators() throws JsonProcessingException {
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		UploadDetail uploadDetail = new UploadDetail();
		uploadDetail.setUrl("https://example.com/test-document.pdf");
		uploadDetail.setKnowledgeType(1);
		uploadDetail.setCustomSeparator(Arrays.asList("\n", "。", "!", "?"));

		DocumentCreateParams request = DocumentCreateParams.builder()
			.uploadDetail(Collections.singletonList(uploadDetail))
			.purpose("retrieval")
			.knowledgeId(TEST_KNOWLEDGE_ID)
			.sentenceSize(150)
			.customSeparator(Arrays.asList("\n", "。", "!", "?"))
			.requestId(requestId)
			.build();

		DocumentObjectResponse response = documentService.createDocument(request);

		assertNotNull(response, "Response should not be null");
		logger.info("Document with custom separators response: {}", mapper.writeValueAsString(response));
	}

}