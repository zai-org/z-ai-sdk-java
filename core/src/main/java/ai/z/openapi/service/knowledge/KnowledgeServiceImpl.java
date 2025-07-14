package ai.z.openapi.service.knowledge;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.api.knowledge.KnowledgeApi;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.utils.RequestSupplier;
import ai.z.openapi.utils.StringUtils;
import retrofit2.Response;

/**
 * Knowledge service implementation
 */
public class KnowledgeServiceImpl implements KnowledgeService {

	private final ZaiClient zAiClient;

	private final KnowledgeApi knowledgeApi;

	public KnowledgeServiceImpl(ZaiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.knowledgeApi = this.zAiClient.retrofit().create(KnowledgeApi.class);
	}

	@Override
	public KnowledgeResponse createKnowledge(KnowledgeBaseParams request) {
		validateCreateKnowledgeParams(request);
		RequestSupplier<KnowledgeBaseParams, KnowledgeInfo> supplier = knowledgeApi::knowledgeCreate;
		return this.zAiClient.executeRequest(request, supplier, KnowledgeResponse.class);
	}

	@Override
	public KnowledgeEditResponse modifyKnowledge(KnowledgeBaseParams request) {
		validateModifyKnowledgeParams(request);
		RequestSupplier<KnowledgeBaseParams, Response<Void>> supplier = (params) -> knowledgeApi
			.knowledgeModify(params.getKnowledgeId(), params);
		return zAiClient.executeRequest(request, supplier, KnowledgeEditResponse.class);
	}

	@Override
	public QueryKnowledgeApiResponse queryKnowledge(QueryKnowledgeRequest request) {
		validateQueryKnowledgeParams(request);
		RequestSupplier<QueryKnowledgeRequest, KnowledgePage> supplier = (params) -> knowledgeApi
			.knowledgeQuery(params.getPage(), params.getSize());
		return zAiClient.executeRequest(request, supplier, QueryKnowledgeApiResponse.class);
	}

	@Override
	public KnowledgeEditResponse deleteKnowledge(String knowledgeId) {
		validateDeleteKnowledgeParams(knowledgeId);
		AsyncResultRetrieveParams params = AsyncResultRetrieveParams.builder().taskId(knowledgeId).build();
		RequestSupplier<AsyncResultRetrieveParams, Response<Void>> supplier = (params1) -> knowledgeApi
			.knowledgeDelete(params1.getTaskId());
		return zAiClient.executeRequest(params, supplier, KnowledgeEditResponse.class);
	}

	@Override
	public KnowledgeUsedResponse checkKnowledgeUsed() {
		RequestSupplier<Void, KnowledgeUsed> supplier = (a) -> knowledgeApi.knowledgeUsed();
		return zAiClient.executeRequest(null, supplier, KnowledgeUsedResponse.class);
	}

	private void validateCreateKnowledgeParams(KnowledgeBaseParams request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
	}

	private void validateModifyKnowledgeParams(KnowledgeBaseParams request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (StringUtils.isEmpty(request.getKnowledgeId())) {
			throw new IllegalArgumentException("knowledge ID cannot be null or empty");
		}
	}

	private void validateQueryKnowledgeParams(QueryKnowledgeRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
	}

	private void validateDeleteKnowledgeParams(String knowledgeId) {
		if (StringUtils.isEmpty(knowledgeId)) {
			throw new IllegalArgumentException("knowledge ID cannot be null or empty");
		}
	}

}