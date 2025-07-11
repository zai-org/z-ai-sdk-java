package ai.z.openapi.service.agents;

import ai.z.openapi.ZAiClient;
import ai.z.openapi.api.agents.AgentsApi;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.RequestSupplier;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Agent completion service implementation
 */
public class AgentServiceImpl implements AgentService {

	private final ZAiClient zAiClient;

	private final AgentsApi agentsApi;

	public AgentServiceImpl(ZAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.agentsApi = zAiClient.retrofit().create(AgentsApi.class);
	}

	@Override
	public ChatCompletionResponse createAgentCompletion(AgentsCompletionRequest request) {
		if (request.getStream()) {
			return streamAgentCompletion(request);
		}
		else {
			return syncAgentCompletion(request);
		}
	}

	@Override
	public Single<ModelData> retrieveAgentAsyncResult(AgentAsyncResultRetrieveParams request) {
		return agentsApi.queryAgentsAsyncResult(request);
	}

	private ChatCompletionResponse streamAgentCompletion(AgentsCompletionRequest request) {
		FlowableRequestSupplier<AgentsCompletionRequest, retrofit2.Call<ResponseBody>> supplier = agentsApi::agentsCompletionStream;
		;
		return this.zAiClient.streamRequest(request, supplier, ChatCompletionResponse.class, ModelData.class);
	}

	private ChatCompletionResponse syncAgentCompletion(AgentsCompletionRequest request) {
		RequestSupplier<AgentsCompletionRequest, ModelData> supplier = agentsApi::agentsCompletionSync;
		return this.zAiClient.executeRequest(request, supplier, ChatCompletionResponse.class);
	}

}