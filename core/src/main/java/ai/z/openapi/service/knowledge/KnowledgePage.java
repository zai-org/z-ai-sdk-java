package ai.z.openapi.service.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a page of knowledge base information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgePage {

	/**
	 * Knowledge base information list
	 */
	@JsonProperty("list")
	private List<KnowledgeInfo> list;

	/**
	 * Total count
	 */
	@JsonProperty("total")
	private Integer total;

}
