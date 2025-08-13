package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;
import java.util.Arrays;

/**
 * Streaming Chat Example
 * Demonstrates how to use ZaiClient for streaming chat conversations
 */
public class ChatCompletionStreamExample {
    
    public static void main(String[] args) {
        // Create client
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();
        
        // Create chat request
        ChatCompletionCreateParams streamRequest = ChatCompletionCreateParams.builder()
            .model(Constants.ModelChatGLM4_5)
            .messages(Arrays.asList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content("Tell me a story")
                    .build()
            ))
            .thinking(ChatThinking.builder().type("enabled").build())
            .stream(true) // Enable streaming response
            .build();
        
        try {
            // Execute streaming request
            ChatCompletionResponse response = client.chat().createChatCompletion(streamRequest);
            
            if (response.isSuccess() && response.getFlowable() != null) {
                System.out.println("Starting streaming response...");
                response.getFlowable().subscribe(
                    data -> {
                        // Process each streaming response chunk
                        if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                            // Get content of current chunk
                            Delta delta = data.getChoices().get(0).getDelta();
                            // Print current chunk
                            System.out.print(delta + "\n");
                        }
                    },
                    error -> System.err.println("\nStream error: " + error.getMessage()),
                    // Process streaming response completion event
                    () -> System.out.println("\nStreaming response completed")
                );
                
                // Wait for streaming response to complete
                Thread.sleep(10000); // Wait for 10 seconds
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}