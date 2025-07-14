package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the document information including success and failure details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentObject {

	/**
	 * Information of successfully uploaded files
	 */
	@JsonProperty("successInfos")
	private List<DocumentSuccessInfo> successInfos;

	/**
	 * Information of failed uploaded files
	 */
	@JsonProperty("failedInfos")
	private List<DocumentFailedInfo> failedInfos;

}
