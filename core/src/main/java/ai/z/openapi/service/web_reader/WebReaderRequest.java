package ai.z.openapi.service.web_reader;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WebReaderRequest implements ClientRequest<WebReaderRequest> {

	/**
	 * The target URL to read and parse content from.
	 */
	@JsonProperty("url")
	private String url;

	/**
	 * Unique request identifier, used for tracing.
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * User ID associated with the request.
	 */
	@JsonProperty("user_id")
	private String userId;

	/**
	 * Timeout in seconds for the reader operation.
	 */
	@JsonProperty("timeout")
	private Integer timeout;

	/**
	 * Whether to bypass cache when reading.
	 */
	@JsonProperty("no_cache")
	private Boolean noCache;

	/**
	 * Return format of the reader output, e.g., markdown or plain.
	 */
	@JsonProperty("return_format")
	private String returnFormat;

	/**
	 * Whether to retain image placeholders in the content.
	 */
	@JsonProperty("retain_images")
	private Boolean retainImages;

	/**
	 * Whether to disable GitHub-Flavored Markdown processing.
	 */
	@JsonProperty("no_gfm")
	private Boolean noGfm;

	/**
	 * Whether to keep image data URLs inline.
	 */
	@JsonProperty("keep_img_data_url")
	private Boolean keepImgDataUrl;

	/**
	 * Whether to include images summary in the result.
	 */
	@JsonProperty("with_images_summary")
	private Boolean withImagesSummary;

	/**
	 * Whether to include links summary in the result.
	 */
	@JsonProperty("with_links_summary")
	private Boolean withLinksSummary;

}