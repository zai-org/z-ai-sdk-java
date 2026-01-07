package ai.z.openapi.service.audio;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class AudioTranscriptionResult {

	@JsonProperty("request_id")
	private String requestId;

	private Long created;

	private String model;

	private String id;

	private String text;

}
