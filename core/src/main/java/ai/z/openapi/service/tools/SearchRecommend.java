package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.deserialize.tools.SearchRecommendDeserializer;

import java.util.Iterator;

/**
 * Represents search query recommendations. This class contains recommended search queries
 * based on search context.
 */
@JsonDeserialize(using = SearchRecommendDeserializer.class)
public class SearchRecommend extends ObjectNode {

	/**
	 * Search round index, defaults to 0.
	 */
	@JsonProperty("index")
	private int index;

	/**
	 * Recommended search query.
	 */
	@JsonProperty("query")
	private String query;

	public SearchRecommend() {
		super(JsonNodeFactory.instance);
	}

	public SearchRecommend(ObjectNode objectNode) {
		super(JsonNodeFactory.instance);
		ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();
		if (objectNode.get("index") != null) {
			this.setIndex(objectNode.get("index").asInt());
		}
		else {
			this.setIndex(0);
		}
		if (objectNode.get("query") != null) {
			this.setQuery(objectNode.get("query").asText());
		}
		else {
			this.setQuery(null);
		}

		Iterator<String> fieldNames = objectNode.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode field = objectNode.get(fieldName);
			this.set(fieldName, field);
		}
	}

	// Getters and Setters

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		this.put("index", index);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;

		this.put("query", query);
	}

}