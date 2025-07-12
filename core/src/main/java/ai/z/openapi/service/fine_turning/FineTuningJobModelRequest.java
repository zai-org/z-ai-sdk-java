package ai.z.openapi.service.fine_turning;

import ai.z.openapi.core.model.ClientRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FineTuningJobModelRequest implements ClientRequest<FineTuningJobModelRequest> {

	@JsonProperty("fine_tuned_model")
	private String fineTunedModel;

}
