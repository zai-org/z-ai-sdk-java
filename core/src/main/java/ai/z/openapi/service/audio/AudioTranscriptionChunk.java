package ai.z.openapi.service.audio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class AudioTranscriptionChunk {

	private Long created;

	private String model;

	private String id;

	private String type;

	private String delta;

}
