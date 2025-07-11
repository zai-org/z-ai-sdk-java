package ai.z.openapi.service.assistant.message.tools.drawing_tool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.ToolsType;
import ai.z.openapi.service.deserialize.JsonTypeField;

/**
 * This class represents a block of drawing tool data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeField("drawing_tool")
public class DrawingToolBlock extends ToolsType {

    /**
     * The drawing tool object that contains input and outputs.
     */
    @JsonProperty("drawing_tool")
    private DrawingTool drawingTool;

    /**
     * The type of tool being called, always "drawing_tool".
     */
    @JsonProperty("type")
    private String type = "drawing_tool";

    // Getters and Setters

    public DrawingTool getDrawingTool() {
        return drawingTool;
    }

    public void setDrawingTool(DrawingTool drawingTool) {
        this.drawingTool = drawingTool;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
