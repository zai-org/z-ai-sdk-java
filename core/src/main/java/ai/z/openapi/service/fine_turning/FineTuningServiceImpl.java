package ai.z.openapi.service.fine_turning;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.api.fine_tuning.FineTuningApi;
import ai.z.openapi.utils.RequestSupplier;

/**
 * Fine-tuning service implementation
 */
public class FineTuningServiceImpl implements FineTuningService {

	private final ZaiClient zAiClient;

	private final FineTuningApi fineTuningApi;

	public FineTuningServiceImpl(ZaiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.fineTuningApi = zAiClient.retrofit().create(FineTuningApi.class);
	}

	@Override
	public CreateFineTuningJobApiResponse createFineTuningJob(FineTuningJobRequest request) {
		RequestSupplier<FineTuningJobRequest, FineTuningJob> supplier = fineTuningApi::createFineTuningJob;
		return this.zAiClient.executeRequest(request, supplier, CreateFineTuningJobApiResponse.class);
	}

	@Override
	public QueryFineTuningEventApiResponse listFineTuningJobEvents(
			QueryFineTuningJobRequest queryFineTuningJobRequest) {
		RequestSupplier<QueryFineTuningJobRequest, FineTuningEvent> supplier = (params) -> fineTuningApi
			.listFineTuningJobEvents(params.getJobId(), params.getLimit(), params.getAfter());
		return this.zAiClient.executeRequest(queryFineTuningJobRequest, supplier,
				QueryFineTuningEventApiResponse.class);
	}

	@Override
	public QueryFineTuningJobApiResponse retrieveFineTuningJob(QueryFineTuningJobRequest queryFineTuningJobRequest) {
		RequestSupplier<QueryFineTuningJobRequest, FineTuningJob> supplier = (params) -> fineTuningApi
			.retrieveFineTuningJob(params.getJobId(), params.getLimit(), params.getAfter());
		return this.zAiClient.executeRequest(queryFineTuningJobRequest, supplier, QueryFineTuningJobApiResponse.class);
	}

	@Override
	public QueryPersonalFineTuningJobApiResponse listPersonalFineTuningJobs(
			QueryPersonalFineTuningJobRequest queryPersonalFineTuningJobRequest) {
		RequestSupplier<QueryPersonalFineTuningJobRequest, PersonalFineTuningJob> supplier = (params) -> fineTuningApi
			.queryPersonalFineTuningJobs(params.getLimit(), params.getAfter());
		return this.zAiClient.executeRequest(queryPersonalFineTuningJobRequest, supplier,
				QueryPersonalFineTuningJobApiResponse.class);
	}

	@Override
	public QueryFineTuningJobApiResponse cancelFineTuningJob(FineTuningJobIdRequest fineTuningJobIdRequest) {
		RequestSupplier<FineTuningJobIdRequest, FineTuningJob> supplier = (params) -> fineTuningApi
			.cancelFineTuningJob(params.getJobId());
		return this.zAiClient.executeRequest(fineTuningJobIdRequest, supplier, QueryFineTuningJobApiResponse.class);
	}

	@Override
	public QueryFineTuningJobApiResponse deleteFineTuningJob(FineTuningJobIdRequest fineTuningJobIdRequest) {
		RequestSupplier<FineTuningJobIdRequest, FineTuningJob> supplier = (params) -> fineTuningApi
			.deleteFineTuningJob(params.getJobId());
		return this.zAiClient.executeRequest(fineTuningJobIdRequest, supplier, QueryFineTuningJobApiResponse.class);
	}

	@Override
	public FineTunedModelsStatusResponse deleteFineTunedModel(FineTuningJobModelRequest fineTuningJobModelRequest) {
		RequestSupplier<FineTuningJobModelRequest, FineTunedModelsStatus> supplier = (params) -> fineTuningApi
			.deleteFineTuningModel(params.getFineTunedModel());
		return this.zAiClient.executeRequest(fineTuningJobModelRequest, supplier, FineTunedModelsStatusResponse.class);
	}

}