package ai.z.openapi.service.knowledge;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class CreateKnowledgeResponse implements ClientResponse<KnowledgeId> {

	private int code;

	private String msg;

	private boolean success;

	private KnowledgeId data;

	private ChatError error;

}
