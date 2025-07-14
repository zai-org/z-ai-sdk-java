package ai.z.openapi.service.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the usage statistics of the knowledge base.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeStatistics {

	/**
	 * Statistical word count
	 */
	@JsonProperty("word_num")
	private Integer wordNum;

	/**
	 * Length
	 */
	@JsonProperty("length")
	private Integer length;

}
