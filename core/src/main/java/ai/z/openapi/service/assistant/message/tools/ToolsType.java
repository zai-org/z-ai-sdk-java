package ai.z.openapi.service.assistant.message.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ai.z.openapi.service.assistant.message.tools.code_interpreter.CodeInterpreterToolBlock;
import ai.z.openapi.service.assistant.message.tools.drawing_tool.DrawingToolBlock;
import ai.z.openapi.service.assistant.message.tools.function.FunctionToolBlock;
import ai.z.openapi.service.assistant.message.tools.retrieval.RetrievalToolBlock;
import ai.z.openapi.service.assistant.message.tools.web_browser.WebBrowserToolBlock;
import ai.z.openapi.service.deserialize.JsonTypeMapping;
import ai.z.openapi.service.deserialize.assistant.message.tools.ToolsTypeDeserializer;

@JsonTypeMapping({
        WebBrowserToolBlock.class,
        RetrievalToolBlock.class,
        FunctionToolBlock.class,
        DrawingToolBlock.class,
        CodeInterpreterToolBlock.class,
})
@JsonDeserialize(using = ToolsTypeDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ToolsType {
}
