package ai.z.openapi.service.batches;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchPage {


    private String object;

    private List<Batch> data;

}
