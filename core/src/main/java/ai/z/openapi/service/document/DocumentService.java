package ai.z.openapi.service.document;

import ai.z.openapi.service.knowledge.document.DocumentCreateParams;
import ai.z.openapi.service.knowledge.document.DocumentEditParams;
import ai.z.openapi.service.knowledge.document.QueryDocumentRequest;
import ai.z.openapi.service.knowledge.document.DocumentDataResponse;
import ai.z.openapi.service.knowledge.document.DocumentEditResponse;
import ai.z.openapi.service.knowledge.document.DocumentObjectResponse;
import ai.z.openapi.service.knowledge.document.QueryDocumentApiResponse;

/**
 * Document service interface
 */
public interface DocumentService {
    
    /**
     * Creates a new document.
     * 
     * @param request the document creation request
     * @return DocumentDataResponse containing the creation result
     */
    DocumentObjectResponse createDocument(DocumentCreateParams request);
    
    /**
     * Modifies an existing document.
     * 
     * @param request the document modification request
     * @return DocumentEditResponse containing the modification result
     */
    DocumentEditResponse modifyDocument(DocumentEditParams request);
    
    /**
     * Deletes a document.
     * 
     * @param documentId the document ID to delete
     * @return DocumentObjectResponse containing the deletion result
     */
    DocumentEditResponse deleteDocument(String documentId);
    
    /**
     * Lists documents.
     * 
     * @param request the query request
     * @return QueryDocumentApiResponse containing the document list
     */
    QueryDocumentApiResponse listDocuments(QueryDocumentRequest request);
    
    /**
     * Retrieves a specific document.
     * 
     * @param documentId the document ID to retrieve
     * @return DocumentDataResponse containing the document details
     */
    DocumentDataResponse retrieveDocument(String documentId);
}