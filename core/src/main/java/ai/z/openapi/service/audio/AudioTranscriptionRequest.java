package ai.z.openapi.service.audio;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.File;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AudioTranscriptionRequest extends CommonRequest implements ClientRequest<AudioTranscriptionRequest> {

	/**
	 * Model code to call (Required)
	 */
	private String model;

	/**
	 * Stream parameter for synchronous/asynchronous calls (Optional) Set to false or omit
	 * for synchronous calls. The model returns all content at once after generation is
	 * complete. Default is false. If set to true, the model will return generated content
	 * in chunks via standard Event Stream. When Event Stream ends, a data: [DONE] message
	 * will be returned.
	 */
	private Boolean stream;

	/**
	 * Audio file to be transcribed (Required) Supported audio file formats: .wav / .mp3
	 * Specification limits: file size ≤ 25 MB, audio duration ≤ 60 seconds
	 */
	private File file;

	/**
	 * Sampling temperature, controls output randomness, must be positive (Optional)
	 * Range: [0.0, 1.0], default value is 0.95 Higher values make output more random and
	 * creative; lower values make output more stable or deterministic It's recommended to
	 * adjust either top_p or temperature parameter based on your use case, but not both
	 * simultaneously
	 */
	private Float temperature;

	/**
	 * Unique identifier for each request (Optional) Passed by the client, must be unique.
	 * Used to distinguish each request. If not provided by the client, the platform will
	 * generate one by default.
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * Unique ID of the end user (Optional) Helps the platform intervene in illegal
	 * activities, generation of illegal inappropriate information, or other abusive
	 * behaviors by end users. ID length requirement: at least 6 characters, maximum 128
	 * characters.
	 */
	@JsonProperty("user_id")
	private String userId;

}
