package ai.z.openapi.service.assistant;

import ai.z.openapi.service.assistant.AssistantParameters;
import ai.z.openapi.service.assistant.AssistantApiResponse;
import ai.z.openapi.service.assistant.conversation.ConversationParameters;
import ai.z.openapi.service.assistant.conversation.ConversationUsageListResponse;
import ai.z.openapi.service.assistant.query_support.AssistantSupportResponse;
import ai.z.openapi.service.assistant.query_support.QuerySupportParams;

/**
 * Assistant service interface
 */
public interface AssistantService {
    
    /**
     * Creates a streaming assistant completion.
     * 
     * @param request the assistant completion request
     * @return AssistantApiResponse containing the completion result
     */
    AssistantApiResponse assistantCompletionStream(AssistantParameters request);
    
    /**
     * Creates a non-streaming assistant completion.
     * 
     * @param request the assistant completion request
     * @return AssistantApiResponse containing the completion result
     */
    AssistantApiResponse assistantCompletion(AssistantParameters request);
    
    /**
     * Queries assistant support status.
     * 
     * @param request the query support request
     * @return AssistantSupportResponse containing the support information
     */
    AssistantSupportResponse querySupport(QuerySupportParams request);
    
    /**
     * Queries conversation usage information.
     * 
     * @param request the conversation parameters
     * @return ConversationUsageListResponse containing the usage information
     */
    ConversationUsageListResponse queryConversationUsage(ConversationParameters request);
}