package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the information of a failed document upload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFailedInfo {

	/**
	 * Reason for upload failure, including: unsupported file format, file size exceeds
	 * limit, knowledge base capacity is full, capacity limit is 500,000 words.
	 */
	@JsonProperty("failReason")
	private String failReason;

	/**
	 * File name
	 */
	@JsonProperty("filename")
	private String filename;

	/**
	 * Knowledge base ID
	 */
	@JsonProperty("documentId")
	private String documentId;

}
