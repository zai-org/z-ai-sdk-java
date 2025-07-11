package ai.z.openapi.service.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.deserialize.ToolCallsDeserializer;
import lombok.*;

import java.util.Iterator;

/**
 * Represents tool calls made by the model during conversation.
 * This class contains information about function calls including the function details,
 * unique identifier, and call type.
 */
@Getter
@JsonDeserialize(using = ToolCallsDeserializer.class)
public class ToolCalls extends ObjectNode {


    @JsonProperty("function")
    private ChatFunctionCall function;

    /**
     * Unique identifier of the function call.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Type of tool called by the model, currently only supports 'function'.
     */
    @JsonProperty("type")
    private String type;

    public ToolCalls() {
        super(JsonNodeFactory.instance);
    }

    public ToolCalls(ObjectNode objectNode) {
        super(JsonNodeFactory.instance);
        ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();
        if (objectNode.get("function") != null) {
            this.setFunction(objectMapper.convertValue(objectNode.get("function"), ChatFunctionCall.class));
        } else {
            this.setFunction(null);
        }
        if (objectNode.get("id") != null) {
            this.setId(objectNode.get("id").asText());
        } else {
            this.setId(null);
        }
        if (objectNode.get("type") != null) {
            this.setType(objectNode.get("type").asText());
        } else {
            this.setType(null);
        }

        Iterator<String> fieldNames = objectNode.fieldNames();

        while(fieldNames.hasNext()) {
            String fieldName = fieldNames.next();

            JsonNode field = objectNode.get(fieldName);
            this.set(fieldName, field);
        }

    }

    public void setFunction(ChatFunctionCall function) {
        this.function = function;
        this.putPOJO("function", function);
    }

    public void setId(String id) {
        this.id = id;
        this.put("id", id);
    }

    public void setType(String type) {
        this.type = type;
        this.put("type", type);
    }
}