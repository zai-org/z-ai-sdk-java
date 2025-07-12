package ai.z.openapi.service.fine_turning;

/**
 * Fine-tuning service interface
 */
public interface FineTuningService {

	/**
	 * Creates a fine-tuning job.
	 * @param request the fine-tuning job creation request
	 * @return CreateFineTuningJobApiResponse containing the created job details
	 */
	CreateFineTuningJobApiResponse createFineTuningJob(FineTuningJobRequest request);

	/**
	 * Lists events for a fine-tuning job.
	 * @param queryFineTuningJobRequest the request parameters for listing events
	 * @return QueryFineTuningEventApiResponse containing the list of events
	 */
	QueryFineTuningEventApiResponse listFineTuningJobEvents(QueryFineTuningJobRequest queryFineTuningJobRequest);

	/**
	 * Retrieves a specific fine-tuning job.
	 * @param queryFineTuningJobRequest the request parameters for retrieving the job
	 * @return QueryFineTuningJobApiResponse containing the job details
	 */
	QueryFineTuningJobApiResponse retrieveFineTuningJob(QueryFineTuningJobRequest queryFineTuningJobRequest);

	/**
	 * Lists personal fine-tuning jobs.
	 * @param queryPersonalFineTuningJobRequest the request parameters for listing
	 * personal jobs
	 * @return QueryPersonalFineTuningJobApiResponse containing the list of personal
	 * fine-tuning jobs
	 */
	QueryPersonalFineTuningJobApiResponse listPersonalFineTuningJobs(
			QueryPersonalFineTuningJobRequest queryPersonalFineTuningJobRequest);

	/**
	 * Cancels a fine-tuning job.
	 * @param fineTuningJobIdRequest the request containing the job ID to cancel
	 * @return QueryFineTuningJobApiResponse containing the cancellation result
	 */
	QueryFineTuningJobApiResponse cancelFineTuningJob(FineTuningJobIdRequest fineTuningJobIdRequest);

	/**
	 * Deletes a fine-tuning job.
	 * @param fineTuningJobIdRequest the request containing the job ID to delete
	 * @return QueryFineTuningJobApiResponse containing the deletion result
	 */
	QueryFineTuningJobApiResponse deleteFineTuningJob(FineTuningJobIdRequest fineTuningJobIdRequest);

	/**
	 * Deletes a fine-tuned model.
	 * @param fineTuningJobModelRequest the request containing the model to delete
	 * @return FineTunedModelsStatusResponse containing the deletion status
	 */
	FineTunedModelsStatusResponse deleteFineTunedModel(FineTuningJobModelRequest fineTuningJobModelRequest);

}