package ai.z.openapi.service.knowledge;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class QueryKnowledgeApiResponse implements ClientResponse<KnowledgePage> {
    private int code;
    private String msg;
    private boolean success;

    private KnowledgePage data;
    private ChatError error;

}
