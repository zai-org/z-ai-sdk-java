package ai.z.openapi;

import ai.z.openapi.core.Constants;
import ai.z.openapi.core.cache.ICache;
import ai.z.openapi.core.cache.LocalCache;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.core.token.TokenManager;
import ai.z.openapi.service.file.FileApiResponse;
import ai.z.openapi.service.file.QueryBatchRequest;
import ai.z.openapi.service.file.QueryFileApiResponse;
import ai.z.openapi.service.file.FileListParams;
import ai.z.openapi.service.file.FileUploadParams;
import ai.z.openapi.service.fine_turning.CreateFineTuningJobApiResponse;
import ai.z.openapi.service.fine_turning.FineTunedModelsStatusResponse;
import ai.z.openapi.service.fine_turning.FineTuningJobIdRequest;
import ai.z.openapi.service.fine_turning.FineTuningJobModelRequest;
import ai.z.openapi.service.fine_turning.FineTuningJobRequest;
import ai.z.openapi.service.fine_turning.QueryFineTuningEventApiResponse;
import ai.z.openapi.service.fine_turning.QueryFineTuningJobApiResponse;
import ai.z.openapi.service.fine_turning.QueryFineTuningJobRequest;
import ai.z.openapi.service.fine_turning.QueryPersonalFineTuningJobApiResponse;
import ai.z.openapi.service.fine_turning.QueryPersonalFineTuningJobRequest;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatFunction;
import ai.z.openapi.service.model.ChatFunctionParameters;
import ai.z.openapi.service.model.ChatMessage;
import java.util.concurrent.atomic.AtomicReference;
import ai.z.openapi.service.model.Usage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatMeta;
import ai.z.openapi.service.model.ChatTool;
import ai.z.openapi.service.model.ChatToolType;
import ai.z.openapi.service.model.Choice;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.service.model.QueryModelResultResponse;
import ai.z.openapi.service.model.WebSearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ai.z.openapi.core.response.HttpxBinaryResponseContent;
import ai.z.openapi.service.audio.AudioCustomizationApiResponse;
import ai.z.openapi.service.audio.AudioCustomizationRequest;
import ai.z.openapi.service.audio.AudioSpeechApiResponse;
import ai.z.openapi.service.audio.AudioSpeechRequest;
import ai.z.openapi.service.batches.BatchCreateParams;
import ai.z.openapi.service.batches.BatchResponse;
import ai.z.openapi.service.batches.QueryBatchResponse;
import ai.z.openapi.service.embedding.EmbeddingCreateParams;
import ai.z.openapi.service.embedding.EmbeddingResponse;
import ai.z.openapi.mock.MockClient;
import ai.z.openapi.service.image.CreateImageRequest;
import ai.z.openapi.service.image.ImageResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Testcontainers
public class ZaiClientTest {

	private final static Logger logger = LoggerFactory.getLogger(ZaiClientTest.class);

	private static final ZaiClient client;

	private static final ZaiConfig zaiConfig;

	static {
		zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZaiClient(zaiConfig);
	}

	// Please customize your own business ID
	private static final String requestIdTemplate = "mycompany-%d";

	private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testTokenManager() {
		ZaiConfig zAiConfig = new ZaiConfig();
		zAiConfig.setApiKey("a.b");
		ICache cache = LocalCache.getInstance();
		TokenManager tokenManager = new TokenManager(cache);
		String token = tokenManager.getToken(zAiConfig);
		assert token != null;
	}

	@Test
	public void testTokenManagerCache() {
		ZaiConfig zAiConfig = new ZaiConfig();
		zAiConfig.setApiKey("a.b");
		String tokenCacheKey = String.format("%s-%s", "zai_oapi_token", zAiConfig.getApiKey());
		ICache cache = LocalCache.getInstance();
		TokenManager tokenManager = new TokenManager(cache);
		String token = tokenManager.getToken(zAiConfig);
		assert token != null;
		assert cache.get(tokenCacheKey) != null;
	}

	/**
	 * Text-to-image
	 */
	@Test
	public void testCreateImage() throws JsonProcessingException {
		CreateImageRequest createImageRequest = new CreateImageRequest();
		createImageRequest.setModel(Constants.ModelCogView);
		createImageRequest
			.setPrompt("Futuristic cloud data center, showcasing advanced technologgy and a high-tech atmosp\n"
					+ "here. The image should depict a spacious, well-lit interior with rows of server racks, glo\n"
					+ "wing lights, and digital displays. Include abstract representattions of data streams and\n"
					+ "onnectivity, symbolizing the essence of cloud computing. Thee style should be modern a\n"
					+ "nd sleek, with a focus on creating a sense of innovaticon and cutting-edge technology\n"
					+ "The overall ambiance should convey the power and effciency of cloud services in a visu\n"
					+ "ally engaging way.");
		createImageRequest.setRequestId("test11111111111111");
		ImageResponse imageApiResponse = client.images().createImage(createImageRequest);
		logger.info("imageApiResponse: {}", mapper.writeValueAsString(imageApiResponse));
	}

	/**
	 * Fine-tuning V4 - Create fine-tuning task
	 */
	@Test
	public void testCreateFineTuningJob() throws JsonProcessingException {
		FineTuningJobRequest request = new FineTuningJobRequest();
		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
		request.setRequestId(requestId);
		request.setModel("chatglm3-6b");
		request.setTraining_file("file-20240118082608327-kp8qr");
		CreateFineTuningJobApiResponse createFineTuningJobApiResponse = client.fineTuning()
			.createFineTuningJob(request);
		logger.info("model output: {}", mapper.writeValueAsString(createFineTuningJobApiResponse));
	}

	/**
	 * Fine-tuning V4 - Query fine-tuning task
	 */
	@Test
	public void testRetrieveFineTuningJobs() throws JsonProcessingException {
		QueryFineTuningJobRequest queryFineTuningJobRequest = new QueryFineTuningJobRequest();
		queryFineTuningJobRequest.setJobId("ftjob-20240429172916475-fb7r9");
		QueryFineTuningJobApiResponse queryFineTuningJobApiResponse = client.fineTuning()
			.retrieveFineTuningJob(queryFineTuningJobRequest);
		logger.info("model output: {}", mapper.writeValueAsString(queryFineTuningJobApiResponse));
	}

	/**
	 * Fine-tuning V4 - Query fine-tuning task
	 */
	@Test
	public void testFueryFineTuningJobsEvents() throws JsonProcessingException {
		QueryFineTuningJobRequest queryFineTuningJobRequest = new QueryFineTuningJobRequest();
		queryFineTuningJobRequest.setJobId("ftjob-20240429172916475-fb7r9");

		QueryFineTuningEventApiResponse queryFineTuningEventApiResponse = client.fineTuning()
			.listFineTuningJobEvents(queryFineTuningJobRequest);
		logger.info("model output: {}", mapper.writeValueAsString(queryFineTuningEventApiResponse));
	}

	/**
	 * testQueryPersonalFineTuningJobs V4 - Query personal fine-tuning tasks
	 */
	@Test
	public void testQueryPersonalFineTuningJobs() throws JsonProcessingException {
		QueryPersonalFineTuningJobRequest queryPersonalFineTuningJobRequest = new QueryPersonalFineTuningJobRequest();
		queryPersonalFineTuningJobRequest.setLimit(1);
		QueryPersonalFineTuningJobApiResponse queryPersonalFineTuningJobApiResponse = client.fineTuning()
			.listPersonalFineTuningJobs(queryPersonalFineTuningJobRequest);
		logger.info("model output: {}", mapper.writeValueAsString(queryPersonalFineTuningJobApiResponse));
	}

	@Test
	public void testBatchesCreate() {
		BatchCreateParams batchCreateParams = new BatchCreateParams("24h", "/v4/chat/completions",
				"20240514_ea19d21b-d256-4586-b0df-e80a45e3c286", new HashMap<String, String>() {
					{
						put("key1", "value1");
						put("key2", "value2");
					}
				});

		BatchResponse batchResponse = client.batches().createBatch(batchCreateParams);
		logger.info("output: {}", batchResponse);
	}

	@Test
	public void testDeleteFineTuningJob() {
		FineTuningJobIdRequest request = FineTuningJobIdRequest.builder().jobId("test").build();
		QueryFineTuningJobApiResponse queryFineTuningJobApiResponse = client.fineTuning().deleteFineTuningJob(request);
		logger.info("output: {}", queryFineTuningJobApiResponse);

	}

	@Test
	public void testCancelFineTuningJob() {
		FineTuningJobIdRequest request = FineTuningJobIdRequest.builder().jobId("test").build();
		QueryFineTuningJobApiResponse queryFineTuningJobApiResponse = client.fineTuning().cancelFineTuningJob(request);
		logger.info("output: {}", queryFineTuningJobApiResponse);

	}

	@Test
	public void testBatchesRetrieve() {
		BatchResponse batchResponse = client.batches().retrieveBatch("batch_1791021399316246528");
		logger.info("output: {}", batchResponse);

	}

	@Test
	public void testDeleteFineTuningModel() {
		FineTuningJobModelRequest request = FineTuningJobModelRequest.builder().fineTunedModel("test").build();

		FineTunedModelsStatusResponse fineTunedModelsStatusResponse = client.fineTuning().deleteFineTunedModel(request);
		logger.info("output: {}", fineTunedModelsStatusResponse);

	}

}
