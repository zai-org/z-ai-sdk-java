package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the document data, including metadata and processing status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentData {

	/**
	 * Knowledge unique ID
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * Segmentation rules
	 */
	@JsonProperty("custom_separator")
	private List<String> customSeparator;

	/**
	 * Segment size
	 */
	@JsonProperty("sentence_size")
	private String sentenceSize;

	/**
	 * File size (bytes)
	 */
	@JsonProperty("length")
	private Integer length;

	/**
	 * File word count
	 */
	@JsonProperty("word_num")
	private Integer wordNum;

	/**
	 * File name
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * File download link
	 */
	@JsonProperty("url")
	private String url;

	/**
	 * Vectorization status 0: Vectorizing 1: Vectorization completed 2: Vectorization
	 * failed
	 */
	@JsonProperty("embedding_stat")
	private Integer embeddingStat;

	/**
	 * Failure reason, present when vectorization fails
	 */
	@JsonProperty("failInfo")
	private DocumentDataFailInfo failInfo;

}
