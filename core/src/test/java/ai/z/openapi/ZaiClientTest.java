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

	@Test
	public void testBatchesList() {
		QueryBatchRequest queryBatchRequest = new QueryBatchRequest();
		queryBatchRequest.setLimit(10);
		QueryBatchResponse queryBatchResponse = client.batches().listBatches(queryBatchRequest);
		logger.info("output: {}", queryBatchResponse);

	}

	@Test
	public void testBatchesCancel() {
		BatchResponse batchResponse = client.batches().cancelBatch("batch_1791021399316246528");
		logger.info("output: {}", batchResponse);
	}

	@Test
	public void testAudioSpeech() throws IOException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");
			// Create a mock file for testing
			File mockFile = new File(System.getProperty("java.io.tmpdir"), "mock_audio_speech.wav");
			if (!mockFile.exists()) {
				mockFile.createNewFile();
			}
			logger.info("testAudioSpeech mock file generation,fileName:{},filePath:{}", mockFile.getName(),
					mockFile.getAbsolutePath());
			return;
		}

		AudioSpeechRequest audioSpeechRequest = AudioSpeechRequest.builder()
			.model(Constants.ModelTTS)
			.input("智谱，你好呀")
			.voice("child")
			.responseFormat("wav")
			.build();
		AudioSpeechApiResponse audioSpeechApiResponse = client.audio().createSpeech(audioSpeechRequest);
		File file = audioSpeechApiResponse.getData();
		file.createNewFile();

		logger.info("testAudioSpeech file generation,fileName:{},filePath:{}",
				audioSpeechApiResponse.getData().getName(), audioSpeechApiResponse.getData().getAbsolutePath());

	}

	@Test
	public void testAudioCustomization() throws IOException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");
			// Create a mock file for testing
			File mockFile = new File(System.getProperty("java.io.tmpdir"), "mock_audio_customization.wav");
			if (!mockFile.exists()) {
				mockFile.createNewFile();
			}
			logger.info("testAudioCustomization mock file generation,fileName:{},filePath:{}", mockFile.getName(),
					mockFile.getAbsolutePath());
			return;
		}

		// Create a test voice data file if it doesn't exist
		File voiceDataFile = new File(System.getProperty("java.io.tmpdir"), "test_case_8s.wav");
		if (!voiceDataFile.exists()) {
			voiceDataFile.createNewFile();
			// Write some dummy content to make it a valid file
			java.nio.file.Files.write(voiceDataFile.toPath(), "dummy audio content".getBytes());
		}

		AudioCustomizationRequest audioCustomizationRequest = AudioCustomizationRequest.builder()
			.model(Constants.ModelTTS)
			.input("智谱，你好呀")
			.voiceText("这是一条测试用例")
			.voiceData(voiceDataFile)
			.responseFormat("wav")
			.build();
		AudioCustomizationApiResponse audioCustomizationApiResponse = client.audio()
			.createCustomSpeech(audioCustomizationRequest);
		File file = audioCustomizationApiResponse.getData();
		file.createNewFile();
		logger.info("testAudioCustomization file generation,fileName:{},filePath:{}",
				audioCustomizationApiResponse.getData().getName(),
				audioCustomizationApiResponse.getData().getAbsolutePath());
	}

	private static String getAsyncTaskId() throws JsonProcessingException {
		// Check if using test API key, skip real API call if so
		if (zaiConfig.getApiKey() != null && zaiConfig.getApiKey().contains("test-api-key")) {
			logger.info("Using test API key, skipping real API call, using mock data");
			List<ChatMessage> messages = new ArrayList<>();
			ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(),
					"Which is more powerful, ChatGLM or you?");
			messages.add(chatMessage);
			String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
			// Function call parameter construction part
			List<ChatTool> chatToolList = new ArrayList<>();
			ChatTool chatTool = new ChatTool();
			chatTool.setType(ChatToolType.FUNCTION.value());
			ChatFunctionParameters chatFunctionParameters = new ChatFunctionParameters();
			chatFunctionParameters.setType("object");
			Map<String, Object> properties = new HashMap<>();
			properties.put("location", new HashMap<String, Object>() {
				{
					put("type", "string");
					put("description", "City, e.g.: Beijing");
				}
			});
			properties.put("unit", new HashMap<String, Object>() {
				{
					put("type", "string");
					put("enum", new ArrayList<String>() {
						{
							add("celsius");
							add("fahrenheit");
						}
					});
				}
			});
			chatFunctionParameters.setProperties(properties);
			ChatFunction chatFunction = ChatFunction.builder()
				.name("get_weather")
				.description("Get the current weather of a location")
				.parameters(chatFunctionParameters)
				.build();
			chatTool.setFunction(chatFunction);
			chatToolList.add(chatTool);
			Map<String, Object> extraJson = new HashMap<>();
			extraJson.put("invoke_method", Constants.INVOKE_METHOD_ASYNC);
			ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
				.model(Constants.ModelChatGLM4)
				.stream(Boolean.FALSE)
				.messages(messages)
				.requestId(requestId)
				.tools(chatToolList)
				.toolChoice("auto")
				.extraJson(extraJson)
				.build();

			// Use mock data
			ChatCompletionResponse invokeModelApiResp = MockClient.mockModelApi(chatCompletionRequest);
			logger.info("Mock response: {}", mapper.writeValueAsString(invokeModelApiResp));
			return invokeModelApiResp.getData().getId();
		}

		List<ChatMessage> messages = new ArrayList<>();
		ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(),
				"Which is more powerful, ChatGLM or you?");
		messages.add(chatMessage);
		String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
		// Function call parameter construction part
		List<ChatTool> chatToolList = new ArrayList<>();
		ChatTool chatTool = new ChatTool();
		chatTool.setType(ChatToolType.FUNCTION.value());
		ChatFunctionParameters chatFunctionParameters = new ChatFunctionParameters();
		chatFunctionParameters.setType("object");
		Map<String, Object> properties = new HashMap<>();
		properties.put("location", new HashMap<String, Object>() {
			{
				put("type", "string");
				put("description", "City, e.g.: Beijing");
			}
		});
		properties.put("unit", new HashMap<String, Object>() {
			{
				put("type", "string");
				put("enum", new ArrayList<String>() {
					{
						add("celsius");
						add("fahrenheit");
					}
				});
			}
		});
		chatFunctionParameters.setProperties(properties);
		ChatFunction chatFunction = ChatFunction.builder()
			.name("get_weather")
			.description("Get the current weather of a location")
			.parameters(chatFunctionParameters)
			.build();
		chatTool.setFunction(chatFunction);
		chatToolList.add(chatTool);
		Map<String, Object> extraJson = new HashMap<>();
		extraJson.put("invoke_method", Constants.INVOKE_METHOD_ASYNC);
		ChatCompletionCreateParams chatCompletionRequest = ChatCompletionCreateParams.builder()
			.model(Constants.ModelChatGLM4)
			.stream(Boolean.FALSE)
			.messages(messages)
			.requestId(requestId)
			.tools(chatToolList)
			.toolChoice("auto")
			.extraJson(extraJson)
			.build();
		ChatCompletionResponse invokeModelApiResp = client.chat().asyncChatCompletion(chatCompletionRequest);
		logger.info("model output: {}", mapper.writeValueAsString(invokeModelApiResp));
		return invokeModelApiResp.getData().getId();
	}

	private static void testQueryResult(String taskId) throws JsonProcessingException {
		AsyncResultRetrieveParams request = new AsyncResultRetrieveParams();
		request.setTaskId(taskId);
		QueryModelResultResponse queryResultResp = client.chat().retrieveAsyncResult(request);
		logger.info("model output {}", mapper.writeValueAsString(queryResultResp));
	}

}
