package ai.z.openapi.service.voiceclone;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceSaveRequest extends CommonRequest implements ClientRequest<VoiceSaveRequest> {


    @JsonProperty("voice_id")
    private String voiceId;
}
