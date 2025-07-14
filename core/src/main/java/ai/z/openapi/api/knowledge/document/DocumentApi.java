package ai.z.openapi.api.knowledge.document;

import ai.z.openapi.service.knowledge.document.DocumentData;
import ai.z.openapi.service.knowledge.document.DocumentEditParams;
import ai.z.openapi.service.knowledge.document.DocumentObject;
import ai.z.openapi.service.knowledge.document.DocumentPage;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Document Management API for intelligent knowledge base content Provides comprehensive
 * document operations within knowledge bases for RAG (Retrieval-Augmented Generation)
 * Supports document upload, automatic processing, vectorization, modification, deletion,
 * and retrieval Enables efficient knowledge management for AI systems with advanced
 * indexing and search capabilities Features automatic content extraction, chunking,
 * embedding generation, and semantic organization
 */
public interface DocumentApi {

	/**
	 * Create and upload a new document to knowledge base with automatic processing
	 * Uploads document content for intelligent processing, content extraction, and
	 * vectorization Supports multiple file formats (PDF, DOC, TXT, MD) with automatic
	 * format detection Features automatic text chunking, embedding generation, and
	 * semantic indexing for optimal retrieval
	 * @param document Multipart document data including file content, metadata, and
	 * processing preferences
	 * @return Document object with unique ID, processing status, extraction results, and
	 * indexing information
	 */
	@POST("files")
	Single<DocumentObject> createDocument(@Body MultipartBody document);

	/**
	 * Modify an existing document with intelligent reprocessing Updates document
	 * metadata, content, or processing settings with automatic re-indexing Supports
	 * content updates, metadata changes, and processing parameter adjustments Triggers
	 * automatic re-vectorization and re-indexing when content is modified
	 * @param documentId Unique identifier of the document to modify
	 * @param documentEditParams Updated document parameters including content, metadata,
	 * and processing settings
	 * @return HTTP response indicating modification success, processing status, and any
	 * validation errors
	 */
	@PUT("document/{document_id}")
	Single<Response<Void>> modifyDocument(@Path("document_id") String documentId,
			@Body DocumentEditParams documentEditParams);

	/**
	 * Delete a document from knowledge base with complete cleanup Permanently removes the
	 * document, its indexed content, generated embeddings, and all associated metadata
	 * Ensures complete removal from vector database and search indices for data
	 * consistency Irreversible operation that affects knowledge base search and retrieval
	 * capabilities
	 * @param documentId Unique identifier of the document to delete
	 * @return HTTP response indicating deletion success, cleanup status, and any
	 * dependency warnings
	 */
	@DELETE("document/{document_id}")
	Single<Response<Void>> deleteDocument(@Path("document_id") String documentId);

	/**
	 * Query and list documents with advanced filtering and pagination Retrieves documents
	 * from knowledge base with comprehensive filter options and sorting Supports
	 * filtering by knowledge base, purpose, processing status, and content type Provides
	 * efficient pagination for large document collections with optimized performance
	 * @param knowledgeId Filter documents by specific knowledge base ID (optional)
	 * @param purpose Filter documents by their intended purpose or category (optional)
	 * @param page Page number for pagination (starts from 1)
	 * @param limit Maximum number of documents to return per page (recommended: 10-50)
	 * @param order Sort order for the document list (e.g., 'created_at', 'updated_at',
	 * 'name')
	 * @return Paginated list of documents with metadata, processing status, and summary
	 * information
	 */
	@GET("files")
	Single<DocumentPage> queryDocumentList(@Query("knowledge_id") String knowledgeId, @Query("purpose") String purpose,
			@Query("page") Integer page, @Query("limit") Integer limit, @Query("order") String order);

	/**
	 * Retrieve comprehensive information about a specific document Gets detailed document
	 * data including content, processing status, embedding information, and usage
	 * statistics Provides insights into document processing results, chunk distribution,
	 * and retrieval performance Essential for monitoring document quality and
	 * troubleshooting knowledge base issues
	 * @param documentId Unique identifier of the document to retrieve
	 * @return Complete document data with content, metadata, processing details,
	 * embedding statistics, and access history
	 */
	@GET("document/{document_id}")
	Single<DocumentData> retrieveDocument(@Path("document_id") String documentId);

}
