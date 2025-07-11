package ai.z.openapi.api.tools;

import ai.z.openapi.service.tools.WebSearchParamsRequest;
import ai.z.openapi.service.tools.WebSearchPro;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public interface ToolsApi {

	@Streaming
	@POST("tools")
	Call<ResponseBody> webSearchStreaming(@Body WebSearchParamsRequest request);

	@POST("tools")
	Single<WebSearchPro> webSearch(@Body WebSearchParamsRequest request);

}
