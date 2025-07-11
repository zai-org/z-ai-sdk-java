package ai.z.openapi.api.chat;


import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ModelData;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ChatApi {


    @Streaming
    @POST("chat/completions")
    Call<ResponseBody> createChatCompletionStream(@Body ChatCompletionCreateParams request);



    @POST("async/chat/completions")
    Single<ModelData> createChatCompletionAsync(@Body ChatCompletionCreateParams request);


    @POST("chat/completions")
    Single<ModelData> createChatCompletion(@Body ChatCompletionCreateParams request);


    @GET("async-result/{id}")
    Single<ModelData> queryAsyncResult(@Path("id") String id);




}




