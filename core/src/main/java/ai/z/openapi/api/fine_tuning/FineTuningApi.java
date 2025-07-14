package ai.z.openapi.api.fine_tuning;

import ai.z.openapi.service.fine_turning.FineTunedModelsStatus;
import ai.z.openapi.service.fine_turning.FineTuningEvent;
import ai.z.openapi.service.fine_turning.FineTuningJob;
import ai.z.openapi.service.fine_turning.FineTuningJobRequest;
import ai.z.openapi.service.fine_turning.PersonalFineTuningJob;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Fine-tuning API for advanced model customization Enables training custom models based
 * on GLM-4, CodeGeeX-4, and other foundation models Supports domain-specific fine-tuning
 * for chat, code generation, embedding, and specialized tasks Features LoRA (Low-Rank
 * Adaptation) and full parameter fine-tuning with efficient data processing Optimized for
 * Chinese and multilingual scenarios with robust training pipeline
 */
public interface FineTuningApi {

	/**
	 * Create a new fine-tuning job for model customization Initiates the training process
	 * for customizing GLM-4, CodeGeeX-4, or other base models Supports various
	 * fine-tuning strategies including LoRA, full parameter tuning, and adapter methods
	 * Enables domain adaptation for specific use cases like coding, conversation, or
	 * specialized knowledge
	 * @param request Fine-tuning job parameters including base model selection, training
	 * dataset, hyperparameters, and optimization settings
	 * @return Fine-tuning job information with unique job ID, status, and initial
	 * training configuration details
	 */
	@POST("fine_tuning/jobs")
	Single<FineTuningJob> createFineTuningJob(@Body FineTuningJobRequest request);

	/**
	 * List events for a specific fine-tuning job with detailed monitoring Retrieves
	 * comprehensive training progress including loss metrics, validation scores, and
	 * status updates Provides real-time monitoring of training convergence, learning rate
	 * schedules, and performance indicators Essential for tracking model quality and
	 * identifying potential training issues
	 * @param fineTuningJobId Unique identifier of the fine-tuning job
	 * @param limit Maximum number of events to return (recommended: 50-100 for efficient
	 * monitoring)
	 * @param after Cursor for pagination to get events after this timestamp
	 * @return List of fine-tuning events with timestamps, training metrics, validation
	 * results, and status details
	 */
	@GET("fine_tuning/jobs/{fine_tuning_job_id}/events")
	Single<FineTuningEvent> listFineTuningJobEvents(@Path("fine_tuning_job_id") String fineTuningJobId,
			@Query("limit") Integer limit, @Query("after") String after);

	/**
	 * Retrieve comprehensive details of a specific fine-tuning job Gets complete
	 * information about job status, training progress, model performance, and
	 * configuration Includes training metrics, validation results, estimated completion
	 * time, and resource usage Provides insights into model convergence and fine-tuning
	 * effectiveness
	 * @param fineTuningJobId Unique identifier of the fine-tuning job
	 * @param limit Maximum number of items to return in nested collections (events,
	 * checkpoints)
	 * @param after Cursor for pagination in nested collections
	 * @return Complete fine-tuning job details including status, metrics, configuration,
	 * and performance indicators
	 */
	@GET("fine_tuning/jobs/{fine_tuning_job_id}")
	Single<FineTuningJob> retrieveFineTuningJob(@Path("fine_tuning_job_id") String fineTuningJobId,
			@Query("limit") Integer limit, @Query("after") String after);

	/**
	 * Query all personal fine-tuning jobs Lists all fine-tuning jobs created by the
	 * current user with pagination support
	 * @param limit Maximum number of jobs to return per page
	 * @param after Cursor for pagination to get jobs after this point
	 * @return Paginated list of personal fine-tuning jobs
	 */
	@GET("fine_tuning/jobs")
	Single<PersonalFineTuningJob> queryPersonalFineTuningJobs(@Query("limit") Integer limit,
			@Query("after") String after);

	/**
	 * Cancel a running fine-tuning job Stops the training process and marks the job as
	 * cancelled
	 * @param fineTuningJobId Unique identifier of the fine-tuning job to cancel
	 * @return Updated fine-tuning job status after cancellation
	 */
	@POST("fine_tuning/jobs/{fine_tuning_job_id}/cancel")
	Single<FineTuningJob> cancelFineTuningJob(@Path("fine_tuning_job_id") String fineTuningJobId);

	/**
	 * Delete a fine-tuning job Permanently removes the fine-tuning job and associated
	 * metadata
	 * @param fineTuningJobId Unique identifier of the fine-tuning job to delete
	 * @return Confirmation of job deletion
	 */
	@DELETE("fine_tuning/jobs/{fine_tuning_job_id}")
	Single<FineTuningJob> deleteFineTuningJob(@Path("fine_tuning_job_id") String fineTuningJobId);

	/**
	 * Delete a fine-tuned model Permanently removes the custom model created from
	 * fine-tuning
	 * @param fineTunedModel Identifier of the fine-tuned model to delete
	 * @return Status confirmation of model deletion
	 */
	@DELETE("fine_tuning/fine_tuned_models/{fine_tuned_model}")
	Single<FineTunedModelsStatus> deleteFineTuningModel(@Path("fine_tuned_model") String fineTunedModel);

}
