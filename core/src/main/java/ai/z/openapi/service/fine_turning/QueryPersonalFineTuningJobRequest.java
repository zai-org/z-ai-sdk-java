package ai.z.openapi.service.fine_turning;

import ai.z.openapi.core.model.ClientRequest;
import lombok.*;

import java.util.HashMap;
import java.util.Map;


/**
 * ClientRequest to create a fine tuning job
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryPersonalFineTuningJobRequest implements ClientRequest<QueryPersonalFineTuningJobRequest> {

    private Integer limit;

    private String after;

}
