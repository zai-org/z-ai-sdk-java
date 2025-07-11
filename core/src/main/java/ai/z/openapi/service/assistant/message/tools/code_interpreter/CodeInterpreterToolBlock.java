package ai.z.openapi.service.assistant.message.tools.code_interpreter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.ToolsType;
import ai.z.openapi.service.deserialize.JsonTypeField;

/**
 * This class represents a block of code tool data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeField("code_interpreter")
public class CodeInterpreterToolBlock extends ToolsType {

    /**
     * The code interpreter object.
     */
    @JsonProperty("code_interpreter")
    private CodeInterpreter codeInterpreter;

    /**
     * The type of tool being called, always "code_interpreter".
     */
    @JsonProperty("type")
    private String type = "code_interpreter";

    // Getters and Setters

    public CodeInterpreter getCodeInterpreter() {
        return codeInterpreter;
    }

    public void setCodeInterpreter(CodeInterpreter codeInterpreter) {
        this.codeInterpreter = codeInterpreter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
