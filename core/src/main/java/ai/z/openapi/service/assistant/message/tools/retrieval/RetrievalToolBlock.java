package ai.z.openapi.service.assistant.message.tools.retrieval;

import ai.z.openapi.service.assistant.message.tools.ToolsType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetrievalToolBlock extends ToolsType {

	/**
	 * The index of the tool call.
	 */
	@JsonProperty("index")
	private Integer index;

	/**
	 * The unique identifier of the tool call.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * An instance of the RetrievalTool class containing the retrieval outputs.
	 */
	@JsonProperty("retrieval")
	private RetrievalTool retrieval;

	/**
	 * The type of tool being used, always set to "retrieval".
	 */
	@JsonProperty("type")
	@Builder.Default
	private String type = "retrieval";

}
