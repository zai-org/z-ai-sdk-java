package ai.z.openapi.service.assistant.conversation;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

/**
 * Response for conversation usage list API calls.
 * This class contains the response data for retrieving conversation usage statistics.
 */
@Data
public class ConversationUsageListResponse implements ClientResponse<ConversationUsageListStatus> {
    /**
     * Response status code.
     */
    private int code;
    
    /**
     * Response message.
     */
    private String msg;
    
    /**
     * Indicates if the request was successful.
     */
    private boolean success;

    /**
     * The conversation usage list data.
     */
    private ConversationUsageListStatus data;

    /**
     * Error information if the request failed.
     */
    private ChatError error;

    public ConversationUsageListResponse() {
    }

    public ConversationUsageListResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
