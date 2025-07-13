package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the failure information of document data processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDataFailInfo {

	/**
	 * Failure code 10001: Knowledge unavailable, knowledge base space has reached the
	 * limit 10002: Knowledge unavailable, knowledge base space has reached the limit
	 * (word count exceeds limit)
	 */
	@JsonProperty("embedding_code")
	private Integer embeddingCode;

	/**
	 * Failure reason
	 */
	@JsonProperty("embedding_msg")
	private String embeddingMsg;

}
