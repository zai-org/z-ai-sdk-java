package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.agents.AgentContent;
import ai.z.openapi.service.agents.AgentMessage;
import ai.z.openapi.service.agents.AgentsCompletionRequest;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessageRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Agent Example
 * Demonstrates how to use ZaiClient for agent-based completions
 */
public class AgentExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();

        syncAgentCompletion(client);
    }
    
    /**
     * Example of synchronous agent completion
     */
    private static void syncAgentCompletion(ZaiClient client) {
        System.out.println("\n=== Synchronous Agent Completion Example ===");
        
        // Create messages for the agent
        List<AgentMessage> messages = new ArrayList<>();
        AgentMessage userMessage = new AgentMessage(
            ChatMessageRole.USER.value(),
                Arrays.asList(AgentContent.ofText("Hello, please translate this to French: How are you today?"))
        );
        messages.add(userMessage);
        
        // Create agent completion request
        AgentsCompletionRequest request = AgentsCompletionRequest.builder()
            .agentId("general_translation") // Using translation agent
            .stream(false) // Non-streaming mode
            .messages(messages)
            .customVariables(JsonNodeFactory.instance.objectNode().put("source_lang", "en").put("target_lang", "cn"))
            .requestId("agent-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Execute request
            ChatCompletionResponse response = client.agents().createAgentCompletion(request);
            
            if (response.isSuccess()) {
                System.out.println("Agent completion successful!");
                
                // Display agent response
                Object content = response.getData().getChoices().get(0).getMessages();
                System.out.println("\nResponse: " + new ObjectMapper().writeValueAsString(content));
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}