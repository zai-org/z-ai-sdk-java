package ai.z.openapi.service.chat;

import ai.z.openapi.ZAiClient;
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

	private final ZAiClient zAiClient;

	private final ChatApi chatApi;

	public ChatServiceImpl(ZAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.chatApi = this.zAiClient.retrofit().create(ChatApi.class);
	}

	@Override
	public ChatCompletionResponse createChatCompletion(ChatCompletionCreateParams request) {
		String paramMsg = validateParams(request);
		if (StringUtils.isNotEmpty(paramMsg)) {
			return new ChatCompletionResponse(-100, String.format("invalid param: %s", paramMsg));
		}
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

	private String validateParams(ChatCompletionCreateParams request) {
		if (request == null) {
			return "request can not be null";
		}
		if (request.getMessages() == null || request.getMessages().isEmpty()) {
			return "message can not be empty";
		}
		if (request.getModel() == null) {
			return "model can not be empty";
		}
		return null;
	}

}