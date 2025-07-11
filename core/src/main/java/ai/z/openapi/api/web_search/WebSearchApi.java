package ai.z.openapi.api.web_search;

import ai.z.openapi.service.web_search.WebSearchDTO;
import ai.z.openapi.service.web_search.WebSearchRequest;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WebSearchApi {

    @POST("web_search")
    Single<WebSearchDTO> webSearch(@Body WebSearchRequest request);

}
