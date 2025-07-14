package ai.z.openapi.service.assistant.message.tools.code_interpreter;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.ToolsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a block of code tool data.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

}
