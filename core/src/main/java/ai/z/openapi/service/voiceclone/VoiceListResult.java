package ai.z.openapi.service.voiceclone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VoiceCloneListResponse implements Serializable {
    private static final long serialVersionUID = 6184357489443914981L;
    @JsonProperty("voice_list")
    private List<VoiceVO> voiceList;
}
