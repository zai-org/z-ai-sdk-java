package ai.z.openapi.service.knowledge;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class KnowledgeResponse implements ClientResponse<KnowledgeInfo> {
    private int code;
    private String msg;
    private boolean success;

    private KnowledgeInfo data;

    private ChatError error;
}
