package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.agents.AgentsCompletionRequest;
import ai.z.openapi.service.agents.AgentAsyncResultRetrieveParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.Choice;
import ai.z.openapi.service.model.ModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Agent Example
 * Demonstrates how to use ZaiClient for agent-based completions
 */
public class AgentExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api.key
        ZaiClient client = ZaiClient.builder().build();
        
        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api.key.your.api.secret")
        //         .build();
        
        // Example 1: Synchronous Agent Completion
        syncAgentCompletion(client);
        
        // Example 2: Streaming Agent Completion
        streamingAgentCompletion(client);
        
        // Example 3: Asynchronous Agent Completion Result Retrieval
        retrieveAsyncAgentResult(client);
    }
    
    /**
     * Example of synchronous agent completion
     */
    private static void syncAgentCompletion(ZaiClient client) {
        System.out.println("\n=== Synchronous Agent Completion Example ===");
        
        // Create messages for the agent
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(
            ChatMessageRole.USER.value(),
            "Hello, please translate this to French: How are you today?"
        );
        messages.add(userMessage);
        
        // Create agent completion request
        AgentsCompletionRequest request = AgentsCompletionRequest.builder()
            .agentId("general_translation") // Using translation agent
            .stream(false) // Non-streaming mode
            .messages(messages)
            .requestId("agent-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Execute request
            ChatCompletionResponse response = client.agents().createAgentCompletion(request);
            
            if (response.isSuccess()) {
                System.out.println("Agent completion successful!");
                
                // Display agent response
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                System.out.println("\nResponse: " + content);
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of streaming agent completion
     */
    private static void streamingAgentCompletion(ZaiClient client) {
        System.out.println("\n=== Streaming Agent Completion Example ===");
        
        // Create messages for the agent
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(
            ChatMessageRole.USER.value(),
            "Please translate this to Spanish: The weather is beautiful today and I'm planning to go for a walk in the park."
        );
        messages.add(userMessage);
        
        // Create agent completion request with streaming
        AgentsCompletionRequest request = AgentsCompletionRequest.builder()
            .agentId("general_translation") // Using translation agent
            .stream(true) // Enable streaming
            .messages(messages)
            .requestId("agent-stream-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Execute streaming request
            ChatCompletionResponse response = client.agents().createAgentCompletion(request);
            
            if (response.isSuccess() && response.getFlowable() != null) {
                System.out.println("Streaming agent completion started...");
                
                // Track streaming progress
                AtomicInteger messageCount = new AtomicInteger(0);
                AtomicBoolean isFirst = new AtomicBoolean(true);
                StringBuilder fullContent = new StringBuilder();
                
                // Subscribe to the stream
                response.getFlowable().doOnNext(modelData -> {
                    if (isFirst.getAndSet(false)) {
                        System.out.println("Receiving stream response:");
                    }
                    
                    if (modelData.getChoices() != null && !modelData.getChoices().isEmpty()) {
                        Choice choice = modelData.getChoices().get(0);
                        if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
                            String content = choice.getDelta().getContent();
                            System.out.print(content);
                            fullContent.append(content);
                            messageCount.incrementAndGet();
                        }
                    }
                })
                .doOnComplete(() -> {
                    System.out.println("\n\nStream completed. Received " + messageCount.get() + " chunks.");
                    System.out.println("Full response length: " + fullContent.length() + " characters");
                })
                .blockingSubscribe();
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of retrieving asynchronous agent completion result
     * Note: You need to replace the taskId with a real task ID from a previous async request
     */
    private static void retrieveAsyncAgentResult(ZaiClient client) {
        System.out.println("\n=== Retrieve Async Agent Result Example ===");
        
        // Replace with a real task ID from a previous async request
        String taskId = "your-task-id-here";
        String agentId = "general_translation";
        
        System.out.println("Retrieving result for task ID: " + taskId);
        System.out.println("Note: In a real application, replace 'your-task-id-here' with an actual task ID.");
        
        // Create retrieve params
        AgentAsyncResultRetrieveParams retrieveParams = AgentAsyncResultRetrieveParams.builder()
            .taskId(taskId)
            .agentId(agentId)
            .requestId("agent-retrieve-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Skip the actual API call in this example to avoid errors with a fake task ID
            if (!taskId.equals("your-task-id-here")) {
                // Execute request to retrieve result
                ModelData result = client.agents().retrieveAgentAsyncResult(retrieveParams).blockingGet();
                
                System.out.println("Retrieved agent result:");
                System.out.println("Content: " + result.getChoices().get(0).getMessage().getContent());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            System.err.println("Note: This is expected for a non-existent task ID.");
        }
    }
}