package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.deserialize.tools.SearchIntentDeserializer;

import java.util.Iterator;

/**
 * Represents search intent information for web search operations.
 * This class contains details about search queries, intent analysis, and keywords.
 */
@JsonDeserialize(using = SearchIntentDeserializer.class)
public class SearchIntent extends ObjectNode {

    /**
     * Search round index, defaults to 0.
     */
    @JsonProperty("index")
    private int index;

    /**
     * Optimized search query.
     */
    @JsonProperty("query")
    private String query;

    /**
     * Determined intent type.
     */
    @JsonProperty("intent")
    private String intent;

    /**
     * Search keywords.
     */
    @JsonProperty("keywords")
    private String keywords;

    public SearchIntent() {
        super(JsonNodeFactory.instance);
    }

    public SearchIntent(ObjectNode objectNode) {
        super(JsonNodeFactory.instance);
        ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();
        if (objectNode.get("index") != null) {
            this.setIndex(objectNode.get("index").asInt());
        } else {
            this.setIndex(0);
        }
        if (objectNode.get("query") != null) {
            this.setQuery(objectNode.get("query").asText());
        } else {
            this.setQuery(null);
        }
        if (objectNode.get("intent") != null) {
            this.setIntent(objectNode.get("intent").asText());
        } else {
            this.setIntent(null);
        }
        if (objectNode.get("keywords") != null) {
            this.setKeywords(objectNode.get("keywords").asText());
        } else {
            this.setKeywords(null);
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

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
        this.put("intent", intent);
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
        this.put("keywords", keywords);
    }
}
