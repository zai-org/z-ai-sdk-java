package ai.z.openapi.mock;

import ai.z.openapi.service.embedding.Embedding;
import ai.z.openapi.service.audio.AudioTranscriptionsRequest;
import ai.z.openapi.service.embedding.EmbeddingResponse;
import ai.z.openapi.service.embedding.EmbeddingCreateParams;
import ai.z.openapi.service.embedding.EmbeddingResult;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.Choice;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.service.model.Usage;
import ai.z.openapi.service.tools.WebSearchApiResponse;
import ai.z.openapi.service.tools.WebSearchChoice;
import ai.z.openapi.service.tools.WebSearchMessage;
import ai.z.openapi.service.tools.WebSearchParamsRequest;
import ai.z.openapi.service.tools.WebSearchPro;
import ai.z.openapi.service.web_search.SearchIntentResp;
import ai.z.openapi.service.web_search.WebSearchDTO;
import ai.z.openapi.service.web_search.WebSearchRequest;
import ai.z.openapi.service.web_search.WebSearchResp;
import ai.z.openapi.service.web_search.WebSearchResponse;
import io.reactivex.Flowable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Mock utility class for simulating API calls in tests
 */
public class MockClient {

	/**
	 * Mock ModelApi method
	 */
	public static ChatCompletionResponse mockModelApi(ChatCompletionCreateParams request) {
		ChatCompletionResponse response = new ChatCompletionResponse();
		response.setSuccess(true);
		response.setCode(200);
		response.setMsg("success");

		if (request.getStream() != null && request.getStream()) {
			// Streaming response
			List<ModelData> mockData = createMockModelData();
			Flowable<ModelData> flowable = Flowable.fromIterable(mockData).delay(100, TimeUnit.MILLISECONDS);
			response.setFlowable(flowable);
		}
		else {
			// Non-streaming response
			ModelData data = createMockModelData().get(0);
			response.setData(data);
		}

		return response;
	}

	/**
	 * Mock EmbeddingApi method
	 */
	public static EmbeddingResponse mockEmbeddingApi(EmbeddingCreateParams request) {
		EmbeddingResponse response = new EmbeddingResponse();
		response.setSuccess(true);
		response.setCode(200);
		response.setMsg("success");

		// Create mock EmbeddingResult data
		EmbeddingResult data = createMockEmbeddingResult(request);
		response.setData(data);

		return response;
	}

	/**
	 * Create mock ModelData data list
	 */
	private static List<ModelData> createMockModelData() {
		List<ModelData> dataList = new ArrayList<>();

		ModelData data = new ModelData();
		data.setCreated(System.currentTimeMillis());
		data.setId("mock-chat-" + System.currentTimeMillis());
		data.setRequestId("mock-request-" + System.currentTimeMillis());

		// Create mock choices
		List<Choice> choices = new ArrayList<>();
		Choice choice = new Choice();
		choice.setIndex(0L);
		choice.setFinishReason("stop");

		// Create mock message
		ChatMessage message = new ChatMessage();
		message.setRole(ChatMessageRole.ASSISTANT.value());
		message.setContent("This is a mock AI assistant reply for testing purposes.");

		choice.setMessage(message);
		choices.add(choice);

		data.setChoices(choices);
		dataList.add(data);

		return dataList;
	}

	/**
	 * Create mock EmbeddingResult data
	 */
	private static EmbeddingResult createMockEmbeddingResult(EmbeddingCreateParams request) {
		EmbeddingResult result = new EmbeddingResult();
		result.setModel(request.getModel() != null ? request.getModel() : "embedding-3");
		result.setObject("list");

		// Create mock embedding data
		List<Embedding> embeddings = new ArrayList<>();

		// Create corresponding number of embeddings based on input type
		int count = 1;
		if (request.getInput() instanceof List) {
			count = ((List<?>) request.getInput()).size();
		}

		for (int i = 0; i < count; i++) {
			Embedding embedding = new Embedding();
			embedding.setObject("embedding");
			embedding.setIndex(i);

			// Create mock vector data
			int dimensions = request.getDimensions() != null ? request.getDimensions() : 512;
			List<Double> vector = new ArrayList<>();
			Random random = new Random();
			for (int j = 0; j < dimensions; j++) {
				vector.add(random.nextGaussian());
			}
			embedding.setEmbedding(vector);

			embeddings.add(embedding);
		}

		result.setData(embeddings);

		// Create mock usage information
		Usage usage = new Usage();
		usage.setPromptTokens(10);
		usage.setTotalTokens(10);
		result.setUsage(usage);

		return result;
	}

	/**
	 * Mock Transcriptions API call
	 */
	public static ChatCompletionResponse mockTranscriptionsApi(AudioTranscriptionsRequest request) {
		ChatCompletionResponse response = new ChatCompletionResponse();
		response.setCode(200);
		response.setMsg("success");
		response.setSuccess(true);

		// Create mock transcription result
		List<ModelData> dataList = new ArrayList<>();
		ModelData data = new ModelData();
		data.setCreated(System.currentTimeMillis());
		data.setId("mock-transcription-" + System.currentTimeMillis());
		data.setRequestId("mock-request-" + System.currentTimeMillis());

		// Create mock choices
		List<Choice> choices = new ArrayList<>();
		Choice choice = new Choice();
		choice.setIndex(0L);
		choice.setFinishReason("stop");

		// Create mock transcription text
		ChatMessage message = new ChatMessage();
		message.setRole(ChatMessageRole.ASSISTANT.value());
		message.setContent("This is mock voice transcription text content.");
		choice.setMessage(message);

		choices.add(choice);
		data.setChoices(choices);

		// Create mock usage information
		Usage usage = new Usage();
		usage.setPromptTokens(0);
		usage.setCompletionTokens(10);
		usage.setTotalTokens(10);
		data.setUsage(usage);

		response.setData(data);

		return response;
	}

}
