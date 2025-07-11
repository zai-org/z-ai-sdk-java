package ai.z.openapi.service.batches;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class BatchResponse implements ClientResponse<Batch> {
    private int code;
    private String msg;
    private boolean success;

    private Batch data;

    private ChatError error;
}
