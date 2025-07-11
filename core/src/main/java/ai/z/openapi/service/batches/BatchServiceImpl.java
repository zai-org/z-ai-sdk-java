package ai.z.openapi.service.batches;

import ai.z.openapi.ZAiClient;
import ai.z.openapi.api.batches.BatchesApi;
import ai.z.openapi.service.file.QueryBatchRequest;
import ai.z.openapi.utils.RequestSupplier;

/**
 * Batch service implementation
 */
public class BatchServiceImpl implements BatchService {
    
    private final ZAiClient zAiClient;
    private final BatchesApi batchesApi;
    
    public BatchServiceImpl(ZAiClient zAiClient) {
        this.zAiClient = zAiClient;
        this.batchesApi = zAiClient.retrofit().create(BatchesApi.class);
    }
    
    @Override
    public BatchResponse createBatch(BatchCreateParams batchCreateParams) {
        RequestSupplier<BatchCreateParams, Batch> supplier = batchesApi::batchesCreate;
        return this.zAiClient.executeRequest(batchCreateParams, supplier, BatchResponse.class);
    }
    
    @Override
    public BatchResponse retrieveBatch(BatchRequest request) {
        RequestSupplier<BatchRequest, Batch> supplier = (params) -> batchesApi.batchesRetrieve(params.getBatchId());
        return this.zAiClient.executeRequest(request, supplier, BatchResponse.class);
    }
    
    @Override
    public BatchResponse retrieveBatch(String batchId) {
        BatchRequest request = BatchRequest.builder().batchId(batchId).build();
        return retrieveBatch(request);
    }
    
    @Override
    public QueryBatchResponse listBatches(QueryBatchRequest queryBatchRequest) {
        RequestSupplier<QueryBatchRequest, BatchPage> supplier = (params) -> batchesApi.batchesList(
                params.getAfter(),
                params.getLimit()
        );
        return this.zAiClient.executeRequest(queryBatchRequest, supplier, QueryBatchResponse.class);
    }
    
    @Override
    public BatchResponse cancelBatch(BatchRequest request) {
        RequestSupplier<BatchRequest, Batch> supplier = (params) -> batchesApi.batchesCancel(params.getBatchId());
        return this.zAiClient.executeRequest(request, supplier, BatchResponse.class);
    }
    
    @Override
    public BatchResponse cancelBatch(String batchId) {
        BatchRequest request = BatchRequest.builder().batchId(batchId).build();
        return cancelBatch(request);
    }
}