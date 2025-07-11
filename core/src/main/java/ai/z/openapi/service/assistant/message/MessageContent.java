package ai.z.openapi.service.assistant.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ai.z.openapi.service.deserialize.JsonTypeMapping;
import ai.z.openapi.service.deserialize.assistant.message.MessageContentDeserializer;

@JsonTypeMapping({ ToolsDeltaBlock.class, TextContentBlock.class })
@JsonDeserialize(using = MessageContentDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MessageContent {

}
