package ai.z.openapi.service.batches;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class QueryBatchResponse implements ClientResponse<BatchPage> {

	private int code;

	private String msg;

	private boolean success;

	private BatchPage data;

	private ChatError error;

}
