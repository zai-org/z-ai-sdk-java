package ai.z.openapi.service.chat;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.api.chat.ChatApi;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.service.model.QueryModelResultResponse;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.RequestSupplier;
import ai.z.openapi.utils.StringUtils;
import okhttp3.ResponseBody;

/**
 * Chat completion service implementation
 */
public class ChatServiceImpl implements ChatService {

	private final ZaiClient zAiClient;

	private final ChatApi chatApi;

	public ChatServiceImpl(ZaiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.chatApi = this.zAiClient.retrofit().create(ChatApi.class);
	}

	@Override
	public ChatCompletionResponse createChatCompletion(ChatCompletionCreateParams request) {
		validateParams(request);
		if (request.getStream()) {
			return streamChatCompletion(request);
		}
		else {
			return syncChatCompletion(request);
		}
	}

	@Override
	public ChatCompletionResponse asyncChatCompletion(ChatCompletionCreateParams request) {
		RequestSupplier<ChatCompletionCreateParams, ModelData> supplier = chatApi::createChatCompletionAsync;
		return this.zAiClient.executeRequest(request, supplier, ChatCompletionResponse.class);
	}

	@Override
	public QueryModelResultResponse retrieveAsyncResult(AsyncResultRetrieveParams request) {
		RequestSupplier<AsyncResultRetrieveParams, ModelData> supplier = (params) -> chatApi
			.queryAsyncResult(params.getTaskId());
		// Handle response
		return this.zAiClient.executeRequest(request, supplier, QueryModelResultResponse.class);
	}

	private ChatCompletionResponse streamChatCompletion(ChatCompletionCreateParams request) {
		FlowableRequestSupplier<ChatCompletionCreateParams, retrofit2.Call<ResponseBody>> supplier = chatApi::createChatCompletionStream;
		return this.zAiClient.streamRequest(request, supplier, ChatCompletionResponse.class, ModelData.class);
	}

	private ChatCompletionResponse syncChatCompletion(ChatCompletionCreateParams request) {
		RequestSupplier<ChatCompletionCreateParams, ModelData> supplier = chatApi::createChatCompletion;
		// Handle response
		return this.zAiClient.executeRequest(request, supplier, ChatCompletionResponse.class);
	}

	private void validateParams(ChatCompletionCreateParams request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getMessages() == null || request.getMessages().isEmpty()) {
			throw new IllegalArgumentException("request messages cannot be null or empty");
		}
		if (request.getModel() == null) {
			throw new IllegalArgumentException("request model cannot be null");
		}
	}

}
