package ai.z.openapi.api.knowledge;

import ai.z.openapi.service.knowledge.KnowledgeBaseParams;
import ai.z.openapi.service.knowledge.KnowledgeInfo;
import ai.z.openapi.service.knowledge.KnowledgePage;
import ai.z.openapi.service.knowledge.KnowledgeUsed;
import io.reactivex.Single;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface KnowledgeApi {

	@POST("knowledge")
	Single<KnowledgeInfo> knowledgeCreate(@Body KnowledgeBaseParams knowledgeBaseParams);

	@PUT("knowledge/{knowledge_id}")
	Single<Response<Void>> knowledgeModify(@Path("knowledge_id") String knowledge_id,
			@Body KnowledgeBaseParams knowledgeBaseParams);

	@GET("knowledge")
	Single<KnowledgePage> knowledgeQuery(@Query("page") Integer page, @Query("size") Integer size);

	@DELETE("knowledge/{knowledge_id}")
	Single<Response<Void>> knowledgeDelete(@Path("knowledge_id") String knowledge_id);

	@GET("knowledge/capacity")
	Single<KnowledgeUsed> knowledgeUsed();

}
