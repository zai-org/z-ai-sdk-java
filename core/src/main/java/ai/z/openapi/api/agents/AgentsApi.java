package ai.z.openapi.api.agents;

import ai.z.openapi.service.agents.AgentAsyncResultRetrieveParams;
import ai.z.openapi.service.agents.AgentsCompletionRequest;
import ai.z.openapi.service.model.ModelData;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface AgentsApi {

    @Streaming
    @POST("v1/agents")
    Call<ResponseBody> agentsCompletionStream(@Body AgentsCompletionRequest request);

    @POST("v1/agents")
    Single<ModelData> agentsCompletionSync(@Body AgentsCompletionRequest request);


    @POST("v1/agents/async-result")
    Single<ModelData> queryAgentsAsyncResult(@Body AgentAsyncResultRetrieveParams request);




}




