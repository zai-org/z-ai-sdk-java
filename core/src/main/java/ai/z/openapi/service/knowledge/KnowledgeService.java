package ai.z.openapi.service.knowledge;

/**
 * Knowledge service interface
 */
public interface KnowledgeService {

	/**
	 * Creates a new knowledge base.
	 * @param request the knowledge creation request
	 * @return CreateKnowledgeResponse containing the creation result
	 */
	CreateKnowledgeResponse createKnowledge(KnowledgeBaseParams request);

	/**
	 * Modifies an existing knowledge base.
	 * @param request the knowledge modification request
	 * @return KnowledgeEditResponse containing the modification result
	 */
	KnowledgeEditResponse modifyKnowledge(KnowledgeBaseParams request);

	/**
	 * Queries knowledge.
	 * @param request the knowledge query request
	 * @return QueryKnowledgeApiResponse containing the query result
	 */
	QueryKnowledgeApiResponse queryKnowledge(QueryKnowledgeRequest request);

	/**
	 * Deletes a knowledge base.
	 * @param knowledgeId the knowledge ID to delete
	 * @return KnowledgeResponse containing the deletion result
	 */
	KnowledgeEditResponse deleteKnowledge(String knowledgeId);

	/**
	 * Checks if knowledge is used.
	 * @return KnowledgeUsedResponse containing the usage status
	 */
	KnowledgeUsedResponse checkKnowledgeUsed();

}