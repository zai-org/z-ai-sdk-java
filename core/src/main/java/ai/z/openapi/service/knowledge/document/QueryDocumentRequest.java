package ai.z.openapi.service.knowledge.document;

import ai.z.openapi.core.model.ClientRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryDocumentRequest implements ClientRequest<QueryDocumentRequest> {

	@JsonProperty("knowledge_id")
	private String knowledgeId;

	private String purpose;

	private Integer page;

	private Integer limit;

	private String order;

}
