package ai.z.openapi.service.assistant.message.tools.web_browser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.ToolsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a block for invoking the web browser tool.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebBrowserToolBlock extends ToolsType {

	/**
	 * An instance of the WebBrowser class containing the search input and outputs.
	 */
	@JsonProperty("web_browser")
	private WebBrowser webBrowser;

	/**
	 * The type of tool being used, always set to "web_browser".
	 */
	@JsonProperty("type")
	private String type = "web_browser";

}
