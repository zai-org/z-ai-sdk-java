package ai.z.openapi.service.web_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchResponse implements ClientResponse<WebSearchDTO> {
    private int code;
    private String msg;
    private boolean success;

    private WebSearchDTO data;
    private ChatError error;

}
