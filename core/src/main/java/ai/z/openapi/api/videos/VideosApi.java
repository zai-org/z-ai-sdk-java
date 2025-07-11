package ai.z.openapi.api.videos;

import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideoObject;
import io.reactivex.Single;
import retrofit2.http.*;

public interface VideosApi {

	@POST("videos/generations")
	Single<VideoObject> videoGenerations(@Body VideoCreateParams request);

	@GET("async-result/{id}")
	Single<VideoObject> videoGenerationsResult(@Path("id") String id);

}
