package ai.z.openapi.service.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KnowledgeBaseParams implements ClientRequest<KnowledgeBaseParams> {

	/**
	 * Knowledge base parameter type definition
	 * <p>
	 * Attributes: embedding_id (int): Vector model ID bound to the knowledge base name
	 * (String): Knowledge base name, limited to 100 characters customer_identifier
	 * (String): User identifier, within 32 characters description (String): Knowledge
	 * base description, limited to 500 characters background (String): Background color
	 * icon (String): Knowledge base icon bucket_id (String): Bucket ID, limited to 32
	 * characters
	 */

	@JsonProperty("embedding_id")
	private int embeddingId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("knowledge_id")
	private String knowledgeId;

	@JsonProperty("customer_identifier")
	private String customerIdentifier;

	@JsonProperty("description")
	private String description;

	@JsonProperty("background")
	private String background;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("bucket_id")
	private String bucketId;

}