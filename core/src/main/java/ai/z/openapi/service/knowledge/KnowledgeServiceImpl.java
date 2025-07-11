package ai.z.openapi.service.knowledge;

import ai.z.openapi.ZAiClient;
import ai.z.openapi.api.knowledge.KnowledgeApi;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.utils.RequestSupplier;
import retrofit2.Response;

/**
 * Knowledge service implementation
 */
public class KnowledgeServiceImpl implements KnowledgeService {

    private final ZAiClient zAiClient;
    private final KnowledgeApi knowledgeApi;
    
    public KnowledgeServiceImpl(ZAiClient zAiClient) {
        this.zAiClient = zAiClient;
        this.knowledgeApi = this.zAiClient.retrofit().create(KnowledgeApi.class);
    }
    
    @Override
    public KnowledgeResponse createKnowledge(KnowledgeBaseParams request) {
        RequestSupplier<KnowledgeBaseParams, KnowledgeInfo> supplier = knowledgeApi::knowledgeCreate;
        return this.zAiClient.executeRequest(request, supplier, KnowledgeResponse.class);
    }
    
    @Override
    public KnowledgeEditResponse modifyKnowledge(KnowledgeBaseParams request) {
        RequestSupplier<KnowledgeBaseParams, Response<Void>> supplier = (params) -> knowledgeApi.knowledgeModify(params.getKnowledgeId(), params);
        return zAiClient.executeRequest(request, supplier, KnowledgeEditResponse.class);
    }

    @Override
    public QueryKnowledgeApiResponse queryKnowledge(QueryKnowledgeRequest request) {
        RequestSupplier<QueryKnowledgeRequest, KnowledgePage> supplier = (params) -> knowledgeApi.knowledgeQuery(params.getPage(), params.getSize());
        return zAiClient.executeRequest(request, supplier, QueryKnowledgeApiResponse.class);
    }

    @Override
    public KnowledgeEditResponse deleteKnowledge(String knowledgeId) {
        AsyncResultRetrieveParams params = new AsyncResultRetrieveParams(knowledgeId);
        RequestSupplier<AsyncResultRetrieveParams, Response<Void>> supplier = (params1) -> knowledgeApi.knowledgeDelete(params1.getTaskId());
        return zAiClient.executeRequest(params, supplier, KnowledgeEditResponse.class);
    }

    @Override
    public KnowledgeUsedResponse checkKnowledgeUsed() {
        RequestSupplier<Void, KnowledgeUsed> supplier = (a) -> knowledgeApi.knowledgeUsed();
        return zAiClient.executeRequest(null, supplier, KnowledgeUsedResponse.class);
    }
}