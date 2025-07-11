package ai.z.openapi.service.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.knowledge.KnowledgeStatisticsDeserializer;

import java.util.Iterator;

/**
 * This class represents the usage statistics of the knowledge base.
 */
@JsonDeserialize(using = KnowledgeStatisticsDeserializer.class)
public class KnowledgeStatistics extends ObjectNode {

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

    public KnowledgeStatistics() {
        super(JsonNodeFactory.instance);
    }

    public KnowledgeStatistics(ObjectNode objectNode) {
        super(JsonNodeFactory.instance);
        if (objectNode.has("word_num")) {
            this.setWordNum(objectNode.get("word_num").asInt());
        }else {
            this.setWordNum(null);
        }
        if (objectNode.has("length")) {
            this.setLength(objectNode.get("length").asInt());
        }else {
            this.setLength(null);
        }
        Iterator<String> fieldNames = objectNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode field = objectNode.get(fieldName);
            this.set(fieldName, field);
        }
    }

    // Getters and Setters

    public Integer getWordNum() {
        return wordNum;
    }

    public void setWordNum(Integer wordNum) {
        this.wordNum = wordNum;
        this.put("word_num", wordNum);
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
        this.put("length", length);
    }
}
