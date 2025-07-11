package ai.z.openapi.api.assistant;

import ai.z.openapi.service.assistant.AssistantCompletion;
import ai.z.openapi.service.assistant.AssistantParameters;
import ai.z.openapi.service.assistant.conversation.ConversationUsageListStatus;
import ai.z.openapi.service.assistant.query_support.AssistantSupportStatus;
import ai.z.openapi.service.assistant.conversation.ConversationParameters;
import ai.z.openapi.service.assistant.query_support.QuerySupportParams;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public interface AssistantApi {

	@Streaming
	@POST("assistant")
	Call<ResponseBody> assistantCompletionStream(@Body AssistantParameters request);

	@POST("assistant")
	Single<AssistantCompletion> assistantCompletion(@Body AssistantParameters request);

	@POST("assistant/list")
	Single<AssistantSupportStatus> querySupport(@Body QuerySupportParams request);

	@POST("assistant/conversation/list")
	Single<ConversationUsageListStatus> queryConversationUsage(@Body ConversationParameters request);

}
