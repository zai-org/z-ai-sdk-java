package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

	private String role;

	/**
	 * Message content: String | MessageContent
	 */
	private Object content;

	@JsonProperty("reasoning_content")
	private String reasoningContent;

	private Audio audio;

	private String name;

	@JsonProperty("tool_calls")
	private List<ToolCalls> toolCalls;

	@JsonProperty("tool_call_id")
	private String toolCallId;

	public ChatMessage(String role, Object content) {
		this.setRole(role);
		this.setContent(content);
	}

}
