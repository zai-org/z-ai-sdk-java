package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.deserialize.assistant.AssistantCompletionDeserializer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents the completion data returned by an assistant.
 */
@JsonDeserialize(using = AssistantCompletionDeserializer.class)
public class AssistantCompletion extends ObjectNode {

    /**
     * Request ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * Conversation ID
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * Assistant ID
     */
    @JsonProperty("assistant_id")
    private String assistantId;

    /**
     * Request creation time, Unix timestamp
     */
    @JsonProperty("created")
    private int created;

    /**
     * Return status, including: `completed` indicates generation finished, `in_progress` indicates generating, `failed` indicates generation exception
     */
    @JsonProperty("status")
    private String status;

    /**
     * Error information
     */
    @JsonProperty("last_error")
    private ErrorInfo lastError;

    /**
     * Incremental return information
     */
    @JsonProperty("choices")
    private List<AssistantChoice> choices;

    /**
     * Metadata, extension field
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Token count statistics
     */
    @JsonProperty("usage")
    private CompletionUsage usage;


    public AssistantCompletion() {
        super(JsonNodeFactory.instance);
    }

    public AssistantCompletion(ObjectNode objectNode) {
        super(JsonNodeFactory.instance);

        ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();
        if (objectNode.has("id")) {
            this.setId(objectNode.get("id").asText());
        }else {
            this.setId(null);
        }

        if (objectNode.has("conversation_id")) {
            this.setConversationId(objectNode.get("conversation_id").asText());
        }else {
            this.setConversationId(null);
        }

        if (objectNode.has("assistant_id")) {
            this.setAssistantId(objectNode.get("assistant_id").asText());
        }else {
            this.setAssistantId(null);
        }

        if (objectNode.has("created")) {
            this.setCreated(objectNode.get("created").asInt());
        }else {
            this.setCreated(0);
        }

        if (objectNode.has("status")) {
            this.setStatus(objectNode.get("status").asText());

        }else {
            this.setStatus(null);
        }

        if (objectNode.has("last_error")) {
            this.setLastError(objectMapper.convertValue(objectNode.get("last_error"), ErrorInfo.class));
        }else {
            this.setLastError(null);
        }

        if (objectNode.has("choices")) {
            List<AssistantChoice> choices1 = objectMapper.convertValue(objectNode.get("choices"), new com.fasterxml.jackson.core.type.TypeReference<List<AssistantChoice>>() {
            });

            this.setChoices(choices1);

        }else {
            this.setChoices(null);
        }

        if (objectNode.has("metadata")) {
            this.setMetadata(objectMapper.convertValue(objectNode.get("metadata"), Map.class));
        }else {
            this.setMetadata(null);
        }

        if (objectNode.has("usage")) {
            this.setUsage(objectMapper.convertValue(objectNode.get("usage"), CompletionUsage.class));
        }else {
            this.setUsage(null);
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

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
        this.put("conversation_id", conversationId);
    }

    public String getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
        this.put("assistant_id", assistantId);
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
        this.put("created", created);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.put("status", status);
    }

    public ErrorInfo getLastError() {
        return lastError;
    }

    public void setLastError(ErrorInfo lastError) {
        this.lastError = lastError;
        this.putPOJO("last_error", lastError);
    }

    public List<AssistantChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<AssistantChoice> choices) {
        this.choices = choices;
        ArrayNode jsonNodes = this.putArray("choices");
        if (choices == null) {
            jsonNodes.removeAll();
        }
        else {

            for (AssistantChoice choice : choices) {
                jsonNodes.add(choice);
            }
        }
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        this.putPOJO("metadata", metadata);
    }

    public CompletionUsage getUsage() {
        return usage;
    }

    public void setUsage(CompletionUsage usage) {
        this.usage = usage;
        this.putPOJO("usage", usage);
    }
}
