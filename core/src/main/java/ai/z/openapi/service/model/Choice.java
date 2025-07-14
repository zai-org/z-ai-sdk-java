package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Choice {

	@JsonProperty("finish_reason")
	private String finishReason;

	@JsonProperty("index")
	private Long index;

	@JsonProperty("message")
	private ChatMessage message;

	@JsonProperty("delta")
	private Delta delta;

}
