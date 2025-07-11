package ai.z.openapi.service.audio;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import ai.z.openapi.service.model.SensitiveWordCheckRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AudioTranscriptionsRequest extends CommonRequest  implements ClientRequest<AudioTranscriptionsRequest> {

    /**
     * Model code to call
     */
    private String model;


    /**
     * Synchronous call: false, SSE call: true
     */
    private Boolean stream;

    private File file;


    /**
     * Sampling temperature, controls output randomness, must be positive
     * Range: (0.0,1.0], cannot equal 0, default value is 0.95
     * Higher values make output more random and creative; lower values make output more stable or deterministic
     * It's recommended to adjust either top_p or temperature parameter based on your use case, but not both simultaneously
     */
    private Float temperature;


    /**
     * Sensitive word detection control
     */
    @JsonProperty("sensitive_word_check")
    private SensitiveWordCheckRequest sensitiveWordCheck;

}
