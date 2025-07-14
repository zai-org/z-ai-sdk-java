package ai.z.openapi.service.assistant.message.tools.function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.ToolsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a block of function tool data.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionToolBlock extends ToolsType {

	/**
	 * The function tool object that contains the name, arguments, and outputs.
	 */
	@JsonProperty("function")
	private FunctionTool function;

	/**
	 * The type of tool being called, always "function".
	 */
	@JsonProperty("type")
	private String type = "function";

}
