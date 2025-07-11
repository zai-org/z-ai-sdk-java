package ai.z.openapi.service.assistant.message.tools.function;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the output of a function tool, containing the generated content.
 */
public class FunctionToolOutput {

	/**
	 * The generated content as a string.
	 */
	@JsonProperty("content")
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
