package ai.z.openapi.service.fine_turning;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import lombok.*;

/**
 * ClientRequest to create a fine tuning job
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FineTuningJobRequest implements ClientRequest<FineTuningJobRequest> {

	/**
	 * The ID of an uploaded file that contains training data.
	 */
	@NonNull
	@JsonProperty("training_file")
	String training_file;

	/**
	 * The ID of an uploaded file that contains validation data. Optional.
	 */
	@JsonProperty("validation_file")
	String validationFile;

	/**
	 * The name of the model to fine-tune.
	 */
	@NonNull
	String model;

	/**
	 * The hyperparameters used for the fine-tuning job.
	 */
	Hyperparameters hyperparameters;

	/**
	 * A string of up to 40 characters that will be added to your fine-tuned model name.
	 */
	String suffix;

	/**
	 * Client request ID
	 */
	private String requestId;

}
