package ai.z.openapi.api.fileparsing;

import ai.z.openapi.service.fileparsing.FileParsingDownloadResp;
import ai.z.openapi.service.fileparsing.FileParsingUploadResp;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * File Parsing API
 * Provides functionality for uploading files for parsing, and retrieving the parsing results.
 */
public interface FileParsingApi {

    /**
     * Create a file parsing task.
     * Uploads a file and creates a document parsing job using specific tool type and file type.
     *
     * @param multipartBody File data with metadata including tool_type and file_type
     * @return Information and status of the parsing job
     */
    //@Multipart
    @POST("files/parser/create")
    Single<FileParsingUploadResp> createParseTask(
            @Body MultipartBody multipartBody
    );

    /**
     * Get a file parsing result.
     * Retrieves the parsing result by taskId and format type.
     *
     * @param taskId     The unique task ID for the parsing job
     * @param formatType The format type of the parsing result
     * @return Parsing result content (JSON or raw format)
     */
    @Streaming
    @GET("files/parser/result/{taskId}/{formatType}")
    Call<ResponseBody> downloadParseResult(
            @Path("taskId") String taskId,
            @Path("formatType") String formatType
    );

    /**
     * Executes a synchronous file parsing operation.
     * Uploads a file and returns the parsing result with specified tool and file type.
     *
     * @param multipartBody The multipart request body containing the file and related metadata (tool type, file type)
     * @return Parsing result content as a FileParsingDownloadResp object
     */
    @POST("files/parser/sync")
    Call<FileParsingDownloadResp> syncParse(
            @Body MultipartBody multipartBody
    );

}