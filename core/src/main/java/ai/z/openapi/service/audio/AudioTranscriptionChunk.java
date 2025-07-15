package ai.z.openapi.service.audio;

import com.fasterxml.jackson.annotation.JsonProperty;

import ai.z.openapi.service.model.Choice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class AudioTranscriptionChunk {

	@JsonProperty("choices")
	private List<Choice> choices;

	private Long created;

	private String model;

	private String id;

	private String type;

	private String delta;

}
