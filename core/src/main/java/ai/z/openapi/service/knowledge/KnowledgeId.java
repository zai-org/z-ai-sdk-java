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
public class KnowledgeId {

	/**
	 * Unique identifier for the knowledge base
	 */
	@JsonProperty("id")
	private String id;

}
