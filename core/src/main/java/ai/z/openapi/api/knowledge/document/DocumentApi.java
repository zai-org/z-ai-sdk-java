package ai.z.openapi.api.knowledge.document;

import ai.z.openapi.service.knowledge.document.DocumentData;
import ai.z.openapi.service.knowledge.document.DocumentEditParams;
import ai.z.openapi.service.knowledge.document.DocumentObject;
import ai.z.openapi.service.knowledge.document.DocumentPage;
import ai.z.openapi.service.knowledge.document.QueryDocumentRequest;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.Map;

public interface DocumentApi {

    @POST("files")
    Single<DocumentObject> createDocument(@Body MultipartBody document);

    @PUT("document/{document_id}")
    Single<Response<Void>> modifyDocument(@Path("document_id") String documentId,
                                          @Body DocumentEditParams documentEditParams
    );

    @DELETE("document/{document_id}")
    Single<Response<Void>> deleteDocument(@Path("document_id") String documentId);

    @GET("files")
    Single<DocumentPage> queryDocumentList(
            @Query("knowledge_id") String knowledgeId,
            @Query("purpose") String purpose,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("order") String order
            );

    @GET("document/{document_id}")
    Single<DocumentData> retrieveDocument(@Path("document_id") String documentId);
}
