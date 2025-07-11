package ai.z.openapi.service.fine_turning;


import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class QueryPersonalFineTuningJobApiResponse implements ClientResponse<PersonalFineTuningJob> {
    private int code;
    private String msg;
    private boolean success;

    private PersonalFineTuningJob data;

    private ChatError error;
}
