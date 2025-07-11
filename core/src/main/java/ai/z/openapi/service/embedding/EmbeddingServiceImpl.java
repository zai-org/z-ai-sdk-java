package ai.z.openapi.service.embedding;

import ai.z.openapi.ZAiClient;
import ai.z.openapi.api.embedding.EmbeddingApi;
import ai.z.openapi.utils.RequestSupplier;

/**
 * Embedding service implementation
 */
public class EmbeddingServiceImpl implements EmbeddingService {
    
    private final ZAiClient zAiClient;
    private final EmbeddingApi embeddingApi;
    
    public EmbeddingServiceImpl(ZAiClient zAiClient) {
        this.zAiClient = zAiClient;
        this.embeddingApi = zAiClient.retrofit().create(EmbeddingApi.class);
    }
    
    @Override
    public EmbeddingResponse createEmbeddings(EmbeddingCreateParams request) {
        RequestSupplier<EmbeddingCreateParams, EmbeddingResult> supplier = embeddingApi::createEmbeddings;
        return this.zAiClient.executeRequest(request, supplier, EmbeddingResponse.class);
    }
}