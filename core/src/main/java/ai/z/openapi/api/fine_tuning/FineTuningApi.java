package ai.z.openapi.api.fine_tuning;

import ai.z.openapi.service.fine_turning.FineTunedModelsStatus;
import ai.z.openapi.service.fine_turning.FineTuningEvent;
import ai.z.openapi.service.fine_turning.FineTuningJob;
import ai.z.openapi.service.fine_turning.FineTuningJobRequest;
import ai.z.openapi.service.fine_turning.PersonalFineTuningJob;
import io.reactivex.Single;
import retrofit2.http.*;

public interface FineTuningApi {



    @POST("fine_tuning/jobs")
    Single<FineTuningJob> createFineTuningJob(@Body FineTuningJobRequest request);

    @GET("fine_tuning/jobs/{fine_tuning_job_id}/events")
    Single<FineTuningEvent> listFineTuningJobEvents(@Path("fine_tuning_job_id") String fineTuningJobId, @Query("limit") Integer limit, @Query("after") String after);

    @GET("fine_tuning/jobs/{fine_tuning_job_id}")
    Single<FineTuningJob> retrieveFineTuningJob(@Path("fine_tuning_job_id") String fineTuningJobId,@Query("limit") Integer limit,@Query("after") String after);


    @GET("fine_tuning/jobs")
    Single<PersonalFineTuningJob> queryPersonalFineTuningJobs(@Query("limit") Integer limit
            , @Query("after") String after);



    @POST("fine_tuning/jobs/{fine_tuning_job_id}/cancel")
    Single<FineTuningJob> cancelFineTuningJob(@Path("fine_tuning_job_id") String fineTuningJobId);


    @DELETE("fine_tuning/jobs/{fine_tuning_job_id}")
    Single<FineTuningJob> deleteFineTuningJob(@Path("fine_tuning_job_id") String fineTuningJobId);

    @DELETE("fine_tuning/fine_tuned_models/{fine_tuned_model}")
    Single<FineTunedModelsStatus> deleteFineTuningModel(@Path("fine_tuned_model") String fineTunedModel);

}
