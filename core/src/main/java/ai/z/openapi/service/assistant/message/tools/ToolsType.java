package ai.z.openapi.service.assistant.message.tools;

import ai.z.openapi.service.assistant.message.tools.code_interpreter.CodeInterpreterToolBlock;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ai.z.openapi.service.assistant.message.tools.drawing_tool.DrawingToolBlock;
import ai.z.openapi.service.assistant.message.tools.function.FunctionToolBlock;
import ai.z.openapi.service.assistant.message.tools.retrieval.RetrievalToolBlock;
import ai.z.openapi.service.assistant.message.tools.web_browser.WebBrowserToolBlock;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({ @JsonSubTypes.Type(value = WebBrowserToolBlock.class, name = "web_browser"),
		@JsonSubTypes.Type(value = RetrievalToolBlock.class, name = "retrieval"),
		@JsonSubTypes.Type(value = FunctionToolBlock.class, name = "function"),
		@JsonSubTypes.Type(value = CodeInterpreterToolBlock.class, name = "code_interpreter"),
		@JsonSubTypes.Type(value = DrawingToolBlock.class, name = "drawing_tool") })
public abstract class ToolsType {

}
