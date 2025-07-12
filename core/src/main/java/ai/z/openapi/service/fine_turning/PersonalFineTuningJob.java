package ai.z.openapi.service.fine_turning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Fine-tuning job
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalFineTuningJob {

	String object;

	private List<FineTuningJob> data;

}