package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the information of a successfully uploaded document.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSuccessInfo {

	/**
	 * File ID
	 */
	@JsonProperty("documentId")
	private String documentId;

	/**
	 * File name
	 */
	@JsonProperty("filename")
	private String filename;

}
