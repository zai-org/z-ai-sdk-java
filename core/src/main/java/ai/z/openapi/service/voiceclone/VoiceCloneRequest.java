package ai.z.openapi.service.voiceclone;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request parameters for voice cloning API. This class contains all the necessary
 * parameters for voice cloning operations, including voice name, sample audio text,
 * target preview text, and audio file information.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceCloneRequest extends CommonRequest implements ClientRequest<VoiceCloneRequest> {

    /** Voice name */
    @JsonProperty("voice_name")
    private String voiceName;

    /** Text content corresponding to the sample audio */
    @JsonProperty("voice_text_input")
    private String voiceTextInput;

    /** Target text for preview audio */
    @JsonProperty("voice_text_output")
    private String voiceTextOutput;

    /** File ID of the audio file */
    @JsonProperty("file_id")
    private String fileId;
}
