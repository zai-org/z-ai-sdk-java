package ai.z.openapi.service.fine_turning;

import lombok.Data;

import java.util.List;

/**
 * Fine-tuning job
 */
@Data
public class PersonalFineTuningJob {

	String object;

	private List<FineTuningJob> data;

}