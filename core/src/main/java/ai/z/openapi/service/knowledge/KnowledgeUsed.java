package ai.z.openapi.service.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the usage information of the knowledge base.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeUsed {

	/**
	 * Used amount
	 */
	@JsonProperty("used")
	private KnowledgeStatistics used;

	/**
	 * Total amount of knowledge base
	 */
	@JsonProperty("total")
	private KnowledgeStatistics total;

}
