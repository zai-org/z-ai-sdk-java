package ai.z.openapi.service.voiceclone;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class VoiceDeleteVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("voice_id")
    private String voiceId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("delete_time")
    private Date deleteTime;
}
