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
public class AgentVideoExample {
    
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
                Arrays.asList(AgentContent.ofText("The two figures in the painting gradually approach each other, then kissing passionately, alternating deep and determined intensity")
                , AgentContent.ofImageUrl("https://img-repo-intl.imdr.cn/dr/sample-182141/xoJteuReBtNCdoMoH.jpg!w1080.jpg"))
        );
        messages.add(userMessage);
        
        // Create agent completion request
        AgentsCompletionRequest request = AgentsCompletionRequest.builder()
            .agentId("vidu_template_agent") // Using translation agent
            .stream(false) // Non-streaming mode
            .messages(messages)
            .customVariables(JsonNodeFactory.instance.objectNode().put("template", "french_kiss"))
            .requestId("agent-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Execute request
            ChatCompletionResponse response = client.agents().createAgentCompletion(request);
            
            if (response.isSuccess()) {
                System.out.println("Agent completion successful!");
                System.out.println("\nResponse: " + new ObjectMapper().writeValueAsString(response.getData()));
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}