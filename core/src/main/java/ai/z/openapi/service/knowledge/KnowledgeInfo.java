package ai.z.openapi.service.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.knowledge.KnowledgeInfoDeserializer;

import java.util.Iterator;

/**
 * This class represents the knowledge base information.
 */
@JsonDeserialize(using = KnowledgeInfoDeserializer.class)
public class KnowledgeInfo extends ObjectNode {

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

	public KnowledgeInfo() {
		super(JsonNodeFactory.instance);
	}

	public KnowledgeInfo(ObjectNode objectNode) {
		super(JsonNodeFactory.instance);
		if (objectNode.has("id")) {
			this.setId(objectNode.get("id").asText());
		}
		else {
			this.setId(null);
		}
		if (objectNode.has("embedding_id")) {
			this.setEmbeddingId(objectNode.get("embedding_id").asText());
		}
		else {
			this.setEmbeddingId(null);
		}
		if (objectNode.has("name")) {
			this.setName(objectNode.get("name").asText());
		}
		else {
			this.setName(null);
		}

		if (objectNode.has("customer_identifier")) {
			this.setCustomerIdentifier(objectNode.get("customer_identifier").asText());
		}
		else {
			this.setCustomerIdentifier(null);
		}
		if (objectNode.has("description")) {
			this.setDescription(objectNode.get("description").asText());
		}
		else {
			this.setDescription(null);
		}
		if (objectNode.has("background")) {
			this.setBackground(objectNode.get("background").asText());
		}
		else {
			this.setBackground(null);
		}
		if (objectNode.has("icon")) {
			this.setIcon(objectNode.get("icon").asText());
		}
		else {
			this.setIcon(null);
		}
		if (objectNode.has("bucket_id")) {
			this.setBucketId(objectNode.get("bucket_id").asText());
		}
		else {
			this.setBucketId(null);
		}

		Iterator<String> fieldNames = objectNode.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode field = objectNode.get(fieldName);
			this.set(fieldName, field);
		}
	}

	// Getters and Setters

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		this.put("id", id);
	}

	public String getEmbeddingId() {
		return embeddingId;
	}

	public void setEmbeddingId(String embeddingId) {
		this.embeddingId = embeddingId;
		this.put("embedding_id", embeddingId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.put("name", name);
	}

	public String getCustomerIdentifier() {
		return customerIdentifier;
	}

	public void setCustomerIdentifier(String customerIdentifier) {
		this.customerIdentifier = customerIdentifier;
		this.put("customer_identifier", customerIdentifier);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		this.put("description", description);
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
		this.put("background", background);
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
		this.put("icon", icon);
	}

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
		this.put("bucket_id", bucketId);
	}

}
