package ai.z.openapi.service.chat;

import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.service.model.QueryModelResultResponse;

/**
 * Chat completion service interface
 */
public interface ChatService {

	/**
	 * Creates a chat completion, either streaming or non-streaming based on the request
	 * configuration.
	 * @param request the chat completion request
	 * @return ChatCompletionResponse containing the completion result
	 */
	ChatCompletionResponse createChatCompletion(ChatCompletionCreateParams request);

	/**
	 * Creates an asynchronous chat completion.
	 * @param request the chat completion request
	 * @return ChatCompletionResponse containing the async completion result
	 */
	ChatCompletionResponse asyncChatCompletion(ChatCompletionCreateParams request);

	/**
	 * Retrieves the result of an asynchronous model operation.
	 * @param request the query request for the async result
	 * @return QueryModelResultResponse containing the async operation result
	 */
	QueryModelResultResponse retrieveAsyncResult(AsyncResultRetrieveParams request);

}