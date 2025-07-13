package ai.z.openapi.service.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the knowledge base information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeInfo {

	/**
	 * Knowledge base unique ID
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * Vector model bound to the knowledge base
	 */
	@JsonProperty("embedding_id")
	private String embeddingId;

	/**
	 * Knowledge base name, limited to 100 characters
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * User identifier, within 32 characters
	 */
	@JsonProperty("customer_identifier")
	private String customerIdentifier;

	/**
	 * Knowledge base description, limited to 500 characters
	 */
	@JsonProperty("description")
	private String description;

	/**
	 * Background color: 'blue', 'red', 'orange', 'purple', 'sky'
	 */
	@JsonProperty("background")
	private String background;

	/**
	 * Knowledge base icon: question: question mark, book: book, seal: seal, wrench:
	 * wrench, tag: tag, horn: horn, house: house
	 */
	@JsonProperty("icon")
	private String icon;

	/**
	 * Bucket ID, limited to 32 characters
	 */
	@JsonProperty("bucket_id")
	private String bucketId;

}
