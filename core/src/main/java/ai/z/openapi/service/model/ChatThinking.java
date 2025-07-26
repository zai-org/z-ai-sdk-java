package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatThinking {

	/**
	 * Model thinking type
	 * Only support by GLM-4.5 and above models.
	 * This parameter is used to control whether the model enable the chain of thought.
	 * value: enabled, disabled
	 */
	private String type;

}
