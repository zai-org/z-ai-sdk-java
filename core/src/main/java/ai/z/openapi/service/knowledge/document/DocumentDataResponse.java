package ai.z.openapi.service.knowledge.document;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class DocumentDataResponse implements ClientResponse<DocumentData> {
    private int code;
    private String msg;
    private boolean success;

    private DocumentData data;

    private ChatError error;
}
