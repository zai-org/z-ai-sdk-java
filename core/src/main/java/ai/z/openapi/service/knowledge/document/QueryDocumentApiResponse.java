package ai.z.openapi.service.knowledge.document;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class QueryDocumentApiResponse implements ClientResponse<DocumentPage> {
    private int code;
    private String msg;
    private boolean success;

    private DocumentPage data;
    private ChatError error;

}
