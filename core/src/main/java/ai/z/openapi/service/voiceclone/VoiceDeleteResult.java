package ai.z.openapi.service.voiceclone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Result data for voice deletion operations. This class contains the deletion result
 * information including voice ID and deletion timestamp.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoiceDeleteResult {

	@JsonProperty("voice_id")
	private String voiceId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonProperty("delete_time")
	private Date deleteTime;

}
