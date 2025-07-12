package ai.z.openapi.service.fine_turning;

import ai.z.openapi.core.model.ClientRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * ClientRequest to create a fine tuning job
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryFineTuningJobRequest implements ClientRequest<QueryFineTuningJobRequest> {

	@JsonProperty("job_id")
	private String jobId;

	private Integer limit;

	private String after;

}
