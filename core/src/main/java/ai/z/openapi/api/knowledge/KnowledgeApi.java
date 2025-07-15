package ai.z.openapi.api.knowledge;

import ai.z.openapi.service.knowledge.KnowledgeBaseParams;
import ai.z.openapi.service.knowledge.KnowledgeId;
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

/**
 * Knowledge Base Management API for document storage and retrieval Provides comprehensive
 * knowledge base operations including creation, modification, and querying Enables AI
 * models to access and utilize structured knowledge for enhanced responses
 */
public interface KnowledgeApi {

	/**
	 * Create a new knowledge base Establishes a new knowledge repository for storing and
	 * organizing documents
	 * @param knowledgeBaseParams Configuration parameters including name, description,
	 * and settings
	 * @return Knowledge base information with ID, status, and metadata
	 */
	@POST("knowledge")
	Single<KnowledgeId> knowledgeCreate(@Body KnowledgeBaseParams knowledgeBaseParams);

	/**
	 * Modify an existing knowledge base Updates knowledge base configuration and settings
	 * @param knowledge_id Unique identifier of the knowledge base to modify
	 * @param knowledgeBaseParams Updated configuration parameters
	 * @return HTTP response indicating modification success or failure
	 */
	@PUT("knowledge/{knowledge_id}")
	Single<Response<Void>> knowledgeModify(@Path("knowledge_id") String knowledge_id,
			@Body KnowledgeBaseParams knowledgeBaseParams);

	/**
	 * Query and list knowledge bases with pagination Retrieves a paginated list of
	 * available knowledge bases
	 * @param page Page number for pagination (starting from 1)
	 * @param size Number of knowledge bases to return per page
	 * @return Paginated list of knowledge bases with metadata
	 */
	@GET("knowledge")
	Single<KnowledgePage> knowledgeQuery(@Query("page") Integer page, @Query("size") Integer size);

	/**
	 * Delete a knowledge base Permanently removes the knowledge base and all associated
	 * documents
	 * @param knowledge_id Unique identifier of the knowledge base to delete
	 * @return HTTP response indicating deletion success or failure
	 */
	@DELETE("knowledge/{knowledge_id}")
	Single<Response<Void>> knowledgeDelete(@Path("knowledge_id") String knowledge_id);

	/**
	 * Get knowledge base usage statistics Retrieves information about storage capacity
	 * and usage metrics
	 * @return Knowledge base usage details including capacity and consumption
	 */
	@GET("knowledge/capacity")
	Single<KnowledgeUsed> knowledgeUsed();

}
