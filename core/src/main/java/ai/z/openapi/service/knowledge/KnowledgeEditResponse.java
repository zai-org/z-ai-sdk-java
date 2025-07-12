package ai.z.openapi.service.knowledge;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;
import retrofit2.Response;

/**
 * Response for knowledge base edit operations. This class contains the response data for
 * knowledge base modification requests.
 */
@Data
public class KnowledgeEditResponse implements ClientResponse<Response<Void>> {

	/**
	 * Response status code.
	 */
	private int code;

	/**
	 * Response message.
	 */
	private String msg;

	/**
	 * Indicates if the edit operation was successful.
	 */
	private boolean success;

	/**
	 * The response data (typically void for edit operations).
	 */
	private Response<Void> data;

	/**
	 * Error information if the edit operation failed.
	 */
	private ChatError error;

}
