package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.knowledge.document.DocumentFailedInfoDeserializer;

import java.util.Iterator;

/**
 * This class represents the information of a failed document upload.
 */
@JsonDeserialize(using = DocumentFailedInfoDeserializer.class)
public class DocumentFailedInfo extends ObjectNode {

    /**
     * Reason for upload failure, including: unsupported file format, file size exceeds limit, knowledge base capacity is full, capacity limit is 500,000 words.
     */
    @JsonProperty("failReason")
    private String failReason;

    /**
     * File name
     */
    @JsonProperty("filename")
    private String filename;

    /**
     * Knowledge base ID
     */
    @JsonProperty("documentId")
    private String documentId;

    public DocumentFailedInfo() {
        super(JsonNodeFactory.instance);
    }

    public DocumentFailedInfo(ObjectNode objectNode) {
        super(JsonNodeFactory.instance);
        if (objectNode == null) {
            return;
        }

        if (objectNode.has("failReason")) {
            this.setFailReason(objectNode.get("failReason").asText());
        } else {
            this.setFailReason(null);
        }
        if (objectNode.has("filename")) {
            this.setFilename(objectNode.get("filename").asText());
        } else {
            this.setFilename(null);
        }

        if (objectNode.has("documentId")) {
            this.setDocumentId(objectNode.get("documentId").asText());
        } else {
            this.setDocumentId(null);
        }

        Iterator<String> fieldNames = objectNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode field = objectNode.get(fieldName);
            this.set(fieldName, field);
        }

    }
    // Getters and Setters

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
        this.put("failReason", failReason);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
        this.put("filename", filename);
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        this.put("documentId", documentId);
    }
}
