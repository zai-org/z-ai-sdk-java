package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.deserialize.assistant.CompletionUsageDeserializer;

import java.util.Iterator;

/**
 * This class represents the usage statistics for a completion.
 */
@JsonDeserialize(using = CompletionUsageDeserializer.class)
public class CompletionUsage extends ObjectNode {

	/**
	 * Number of tokens in the input (prompt).
	 */
	@JsonProperty("prompt_tokens")
	private int promptTokens;

	/**
	 * Number of tokens in the output (completion).
	 */
	@JsonProperty("completion_tokens")
	private int completionTokens;

	/**
	 * Total number of tokens used.
	 */
	@JsonProperty("total_tokens")
	private int totalTokens;

	public CompletionUsage() {
		super(JsonNodeFactory.instance);
	}

	public CompletionUsage(ObjectNode objectNode) {
		super(JsonNodeFactory.instance);

		ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();
		if (objectNode.get("prompt_tokens") != null) {
			this.setPromptTokens(objectNode.get("prompt_tokens").asInt());
		}
		else {
			this.setPromptTokens(0);
		}
		if (objectNode.get("completion_tokens") != null) {
			this.setCompletionTokens(objectNode.get("completion_tokens").asInt());
		}
		else {
			this.setCompletionTokens(0);
		}
		if (objectNode.get("total_tokens") != null) {
			this.setTotalTokens(objectNode.get("total_tokens").asInt());
		}
		else {
			this.setTotalTokens(0);
		}

		Iterator<String> fieldNames = objectNode.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode field = objectNode.get(fieldName);
			this.set(fieldName, field);
		}
	}
	// Getters and Setters

	public int getPromptTokens() {
		return promptTokens;
	}

	public void setPromptTokens(int promptTokens) {
		this.promptTokens = promptTokens;
		this.put("prompt_tokens", promptTokens);
	}

	public int getCompletionTokens() {
		return completionTokens;
	}

	public void setCompletionTokens(int completionTokens) {
		this.completionTokens = completionTokens;
		this.put("completion_tokens", completionTokens);
	}

	public int getTotalTokens() {
		return totalTokens;
	}

	public void setTotalTokens(int totalTokens) {
		this.totalTokens = totalTokens;
		this.put("total_tokens", totalTokens);
	}

}
