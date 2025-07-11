package ai.z.openapi.service.knowledge;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class KnowledgeUsedResponse implements ClientResponse<KnowledgeUsed> {
    private int code;
    private String msg;
    private boolean success;

    private KnowledgeUsed data;
    private ChatError error;

}
