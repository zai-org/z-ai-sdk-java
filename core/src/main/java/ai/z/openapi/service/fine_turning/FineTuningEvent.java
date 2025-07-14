package ai.z.openapi.service.fine_turning;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.model.ChatError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * An object representing an event in the lifecycle of a fine-tuning job
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FineTuningEvent {

	private String object;

	@JsonProperty("has_more")
	private Boolean hasMore;

	private List<FineTuningEventData> data;

	private ChatError error;

}
