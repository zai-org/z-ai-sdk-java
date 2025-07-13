package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a page of document data, including a list of document entries and
 * the object type.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPage {

	/**
	 * List of document data entries.
	 */
	@JsonProperty("list")
	private List<DocumentData> list;

	/**
	 * The object type.
	 */
	@JsonProperty("object")
	private String object;

}
