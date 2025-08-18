package ai.z.openapi.service.voiceclone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class VoiceVO {

    @JsonProperty("voice_id")
    private String voiceId;

    @JsonProperty("voice_name")
    private String voiceName;

    @JsonProperty("voice_type")
    private String voiceType;

    @JsonProperty("download_url")
    private String downloadUrl;

    @JsonProperty("create_time")
    private Date createTime;
}
