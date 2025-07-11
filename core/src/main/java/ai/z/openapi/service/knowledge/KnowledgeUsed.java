package ai.z.openapi.service.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.deserialize.knowledge.KnowledgeUsedDeserializer;

import java.util.Iterator;

/**
 * This class represents the usage information of the knowledge base.
 */
@JsonDeserialize(using = KnowledgeUsedDeserializer.class)
public class KnowledgeUsed extends ObjectNode {

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

    public KnowledgeUsed() {
        super(JsonNodeFactory.instance);
    }

    public KnowledgeUsed(ObjectNode objectNode) {
        super(JsonNodeFactory.instance);
        ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();
        if (objectNode.has("used")) {
            this.setUsed(objectMapper.convertValue(objectNode.get("used"), KnowledgeStatistics.class));
        } else {
            this.setUsed(null);
        }
        if (objectNode.has("total")) {
            this.setTotal(objectMapper.convertValue(objectNode.get("total"), KnowledgeStatistics.class));
        } else {
            this.setTotal(null);
        }

        Iterator<String> fieldNames = objectNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode field = objectNode.get(fieldName);
            this.set(fieldName, field);
        }
    }

    // Getters and Setters

    public KnowledgeStatistics getUsed() {
        return used;
    }

    public void setUsed(KnowledgeStatistics used) {
        this.used = used;
        this.set("used", used);
    }

    public KnowledgeStatistics getTotal() {
        return total;
    }

    public void setTotal(KnowledgeStatistics total) {
        this.total = total;
        this.set("total", total);
    }
}
