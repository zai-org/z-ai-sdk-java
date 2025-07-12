package ai.z.openapi.service.fine_turning;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class QueryFineTuningEventApiResponse implements ClientResponse<FineTuningEvent> {

	private int code;

	private String msg;

	private boolean success;

	private FineTuningEvent data;

	private ChatError error;

}
