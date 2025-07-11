package ai.z.openapi;

import ai.z.openapi.core.config.ZAiConfig;
import ai.z.openapi.service.knowledge.KnowledgeBaseParams;
import ai.z.openapi.service.knowledge.KnowledgeEditResponse;
import ai.z.openapi.service.knowledge.KnowledgeInfo;
import ai.z.openapi.service.knowledge.KnowledgeResponse;
import ai.z.openapi.service.knowledge.KnowledgeUsedResponse;
import ai.z.openapi.service.knowledge.QueryKnowledgeApiResponse;
import ai.z.openapi.service.knowledge.QueryKnowledgeRequest;
import ai.z.openapi.service.knowledge.document.DocumentCreateParams;
import ai.z.openapi.service.knowledge.document.DocumentDataResponse;
import ai.z.openapi.service.knowledge.document.DocumentEditParams;
import ai.z.openapi.service.knowledge.document.DocumentEditResponse;
import ai.z.openapi.service.knowledge.document.DocumentObjectResponse;
import ai.z.openapi.service.knowledge.document.QueryDocumentApiResponse;
import ai.z.openapi.service.knowledge.document.QueryDocumentRequest;
import ai.z.openapi.service.knowledge.document.UploadDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assumptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.Collections;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestKnowledgeClientApiService {

	private final static Logger logger = LoggerFactory.getLogger(TestKnowledgeClientApiService.class);

	private static final ZAiConfig zaiConfig;

	private static final ZaiClient client;

	static {
		zaiConfig = new ZAiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZaiClient(zaiConfig);
	}
	private KnowledgeResponse knowledgeResponse;

	private DocumentObjectResponse documentObjectResponse;

	@Test
	@Order(1)
	public void testKnowledgeCreate() {
		// Check if using test API key, use mock data if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, using mock data");
			// Create mock KnowledgeResponse
			knowledgeResponse = new KnowledgeResponse();
			KnowledgeInfo knowledgeInfo = new KnowledgeInfo();
			knowledgeInfo.setId("mock-knowledge-id-123");
			knowledgeInfo.setName("test");
			knowledgeInfo.setDescription("Test");
			knowledgeResponse.setData(knowledgeInfo);
			knowledgeResponse.setSuccess(true);
			logger.info("Mock knowledgeCreate result: {}", knowledgeResponse);
			return;
		}

		KnowledgeBaseParams build = KnowledgeBaseParams.builder()
			.embeddingId(1)
			.name("test")
			.description("Test")
			.icon("question")
			.background("blue")
			.build();
		// Set relevant properties of params
		KnowledgeResponse apply = client.knowledge().createKnowledge(build);

		logger.info("knowledgeCreate result: {}", apply);
		knowledgeResponse = apply;
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);
	}

	@Test
	@Order(2)
	public void testKnowledgeModify() {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skipping test: Using test API key, real API key required for this test");

		KnowledgeBaseParams build = KnowledgeBaseParams.builder()
			.knowledgeId(knowledgeResponse.getData().getId())
			.embeddingId(1)
			.name("Test1")
			.description("Test")
			.icon("question")
			.background("blue")
			.build();
		KnowledgeEditResponse apply = client.knowledge().modifyKnowledge(build);

		logger.info("knowledgeModify result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);
	}

	@Test
	@Order(3)
	public void testCreateDocumentFile() throws JsonProcessingException {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skipping test: Using test API key, real API key required for this test");

		String filePath = "file.xlsx";

		String path = ClassLoader.getSystemResource(filePath).getPath();
		String purpose = "retrieval";

		DocumentCreateParams build = DocumentCreateParams.builder()
			.filePath(path)
			.knowledgeId(knowledgeResponse.getData().getId())
			.sentenceSize(202)
			.purpose(purpose)
			.customSeparator(null)
			.build();
		// Set relevant properties of params
		DocumentObjectResponse apply = client.documents().createDocument(build);

		logger.info("createDocument result: {}", apply);
		documentObjectResponse = apply;
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);

	}

	@Test
	@Order(4)
	public void testCreateDocumentUploadDetail() throws JsonProcessingException {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skipping test: Using test API key, real API key required for this test");

		String purpose = "retrieval";
		UploadDetail uploadDetail = new UploadDetail();
		uploadDetail.setUrl("http://www.baidu.com");
		DocumentCreateParams build = DocumentCreateParams.builder()
			.knowledgeId(knowledgeResponse.getData().getId())
			.sentenceSize(202)
			.purpose(purpose)
			.uploadDetail(Collections.singletonList(uploadDetail))
			.customSeparator(null)
			.build();
		// Set relevant properties of params
		DocumentObjectResponse apply = client.documents().createDocument(build);

		logger.info("testCreateDocumentUploadDetail result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);

	}

	@Test
	@Order(5)
	public void testKnowledgeQuery() {
		QueryKnowledgeRequest build = QueryKnowledgeRequest.builder().page(1).size(10).build();
		// Set relevant properties of params
		QueryKnowledgeApiResponse apply = client.knowledge().queryKnowledge(build);

		logger.info("knowledgeQuery result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);
	}

	@Test
	@Order(5)
	public void testModifyDocument() {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skip test: Using test API key, real API key required to execute this test");

		DocumentEditParams build = DocumentEditParams.builder()
			.id(documentObjectResponse.getData().getSuccessInfos().get(0).getDocumentId())
			.knowledgeType(1)
			.sentenceSize(202)
			.build();
		// Set relevant properties of params
		DocumentEditResponse apply = client.documents().modifyDocument(build);

		logger.info("modifyDocument result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);

	}

	@Test
	@Order(7)
	public void testRetrieveDocument() {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skip test: Using test API key, real API key required to execute this test");

		DocumentEditParams build = DocumentEditParams.builder()
			.id(documentObjectResponse.getData().getSuccessInfos().get(0).getDocumentId())
			.sentenceSize(203)
			.build();
		// Set relevant properties of params
		DocumentDataResponse apply = client.documents().retrieveDocument(build.getId());

		logger.info("retrieveDocument result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);

	}

	@Test
	@Order(7)
	public void testQueryDocumentList() {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call");
			return;
		}

		QueryDocumentRequest build = QueryDocumentRequest.builder()
			.knowledgeId(knowledgeResponse.getData().getId())
			.purpose("retrieval")
			.page(1)
			.limit(10)
			.build();
		// Set relevant properties of params
		QueryDocumentApiResponse apply = client.documents().listDocuments(build);

		logger.info("queryDocumentList result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);

	}

	@Test
	@Order(997)
	public void testDeleteDocument() {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skip test: Using test API key, real API key required to execute this test");

		DocumentEditParams build = DocumentEditParams.builder()
			.id(documentObjectResponse.getData().getSuccessInfos().get(0).getDocumentId())
			.build();
		// Set relevant properties of params
		DocumentEditResponse apply = client.documents().deleteDocument(build.getId());

		logger.info("deleteDocument result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);
	}

	@Test
	@Order(998)
	public void testKnowledgeDelete() {
		// Check if using real API key, skip test if using test key
		Assumptions.assumeTrue(zaiConfig.getApiKey() != null && !zaiConfig.getApiKey().contains("test-api-key"),
				"Skip test: Using test API key, real API key required to execute this test");

		KnowledgeBaseParams build = KnowledgeBaseParams.builder()
			.knowledgeId(knowledgeResponse.getData().getId())
			.build();
		// Set relevant properties of params
		KnowledgeEditResponse apply = client.knowledge().deleteKnowledge(build.getKnowledgeId());

		logger.info("knowledgeDelete result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);
	}

	@Test
	@Order(6)
	public void testKnowledgeUsed() {
		KnowledgeUsedResponse apply = client.knowledge().checkKnowledgeUsed();

		logger.info("knowledgeUsed result: {}", apply);
		// Add assertions here to verify the return result
		Assertions.assertNotNull(apply);
	}

}
