package ai.z.openapi.service.voiceclone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Result data for voice deletion operations. This class contains the deletion
 * result information including voice ID and deletion timestamp.
 */
@Data
@AllArgsConstructor
public class VoiceDeleteResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("voice_id")
    private String voiceId;

    @JsonProperty("delete_time")
    private Date deleteTime;
}
