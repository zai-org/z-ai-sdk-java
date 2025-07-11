package ai.z.openapi.service.knowledge.document;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class DocumentObjectResponse implements ClientResponse<DocumentObject> {
    private int code;
    private String msg;
    private boolean success;

    private DocumentObject data;

    private ChatError error;
}
