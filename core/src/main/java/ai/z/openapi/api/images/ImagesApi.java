package ai.z.openapi.api.images;

import ai.z.openapi.service.image.CreateImageRequest;
import ai.z.openapi.service.image.ImageResult;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImagesApi {


    @POST("images/generations")
    Single<ImageResult> createImage(@Body CreateImageRequest request);


}
