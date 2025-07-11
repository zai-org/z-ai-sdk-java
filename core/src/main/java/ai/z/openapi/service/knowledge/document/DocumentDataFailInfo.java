package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.knowledge.document.DocumentDataFailInfoDeserializer;

import java.util.Iterator;

/**
 * This class represents the failure information of document data processing.
 */
@JsonDeserialize(using = DocumentDataFailInfoDeserializer.class)
public class DocumentDataFailInfo extends ObjectNode {

    /**
     * Failure code
     * 10001: Knowledge unavailable, knowledge base space has reached the limit
     * 10002: Knowledge unavailable, knowledge base space has reached the limit (word count exceeds limit)
     */
    @JsonProperty("embedding_code")
    private Integer embeddingCode;

    /**
     * Failure reason
     */
    @JsonProperty("embedding_msg")
    private String embeddingMsg;

    public DocumentDataFailInfo() {
        super(JsonNodeFactory.instance);
    }

    public DocumentDataFailInfo(ObjectNode objectNode) {
        super(JsonNodeFactory.instance);

        if (objectNode.get("embedding_code") != null) {
            this.setEmbeddingCode(objectNode.get("embedding_code").asInt());
        }else{
            this.setEmbeddingCode(null);
        }

        if (objectNode.get("embedding_msg") != null) {
            this.setEmbeddingMsg(objectNode.get("embedding_msg").asText());

        }else{
            this.setEmbeddingMsg(null);
        }
        Iterator<String> fieldNames = objectNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode field = objectNode.get(fieldName);
            this.set(fieldName, field);
        }
    }
    // Getters and Setters

    public Integer getEmbeddingCode() {
        return embeddingCode;
    }

    public void setEmbeddingCode(Integer embeddingCode) {
        this.embeddingCode = embeddingCode;
        this.put("embedding_code", embeddingCode);
    }

    public String getEmbeddingMsg() {
        return embeddingMsg;
    }

    public void setEmbeddingMsg(String embeddingMsg) {
        this.embeddingMsg = embeddingMsg;
        this.put("embedding_msg", embeddingMsg);
    }
}
