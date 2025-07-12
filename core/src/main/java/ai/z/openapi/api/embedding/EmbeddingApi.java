package ai.z.openapi.api.embedding;

import ai.z.openapi.service.embedding.EmbeddingCreateParams;
import ai.z.openapi.service.embedding.EmbeddingResult;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EmbeddingApi {

	@POST("embeddings")
	Single<EmbeddingResult> createEmbeddings(@Body EmbeddingCreateParams request);

}
