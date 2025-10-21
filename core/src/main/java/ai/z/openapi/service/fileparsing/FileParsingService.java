package ai.z.openapi.service.fileparsing;

/**
 * File parsing service interface
 */
public interface FileParsingService {

	/**
	 * Submits a file parsing task to the server.
	 * @param request The file parsing upload request
	 * @return FileParsingUploadResp containing the parsing task info
	 */
	FileParsingResponse createParseTask(FileParsingUploadReq request);

	/**
	 * Retrieves the result of a parsing task.
	 * @param request The parsing result query request (can include taskId, formatType
	 * etc)
	 * @return FileParsingDownloadResp containing the result content
	 */
	FileParsingDownloadResponse getParseResult(FileParsingDownloadReq request);

}