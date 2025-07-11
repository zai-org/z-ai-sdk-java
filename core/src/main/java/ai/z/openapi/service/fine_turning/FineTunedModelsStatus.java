package ai.z.openapi.service.fine_turning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the status of a fine-tuned model. This class contains information about
 * model deletion status and identification.
 */
@Data
public class FineTunedModelsStatus {

	/**
	 * Request ID.
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * Model name.
	 */
	@JsonProperty("model_name")
	private String modelName;

	/**
	 * Deletion status: deleting (in progress), deleted (completed).
	 */
	@JsonProperty("delete_status")
	private String deleteStatus;

}
