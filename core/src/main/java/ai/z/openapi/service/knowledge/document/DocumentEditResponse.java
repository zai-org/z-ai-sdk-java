package ai.z.openapi.service.knowledge.document;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;
import retrofit2.Response;

@Data
public class DocumentEditResponse implements ClientResponse<Response<Void>> {

	private int code;

	private String msg;

	private boolean success;

	private Response<Void> data;

	private ChatError error;

}
