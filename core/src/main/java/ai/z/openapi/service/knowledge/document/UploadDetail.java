package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the details required for uploading a file to the knowledge base.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadDetail {

	/**
	 * URL of the file to be uploaded.
	 */
	@JsonProperty("url")
	private String url;

	/**
	 * Knowledge type identifier.
	 */
	@JsonProperty("knowledge_type")
	private int knowledgeType;

	/**
	 * Optional file name.
	 */
	@JsonProperty("file_name")
	private String fileName;

	/**
	 * Optional sentence size for processing.
	 */
	@JsonProperty("sentence_size")
	private Integer sentenceSize;

	/**
	 * Optional list of custom separators.
	 */
	@JsonProperty("custom_separator")
	private List<String> customSeparator;

	/**
	 * Optional callback URL for notifications.
	 */
	@JsonProperty("callback_url")
	private String callbackUrl;

	/**
	 * Optional callback headers for the callback request.
	 */
	@JsonProperty("callback_header")
	private Map<String, String> callbackHeader;

}
