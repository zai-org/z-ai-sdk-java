package ai.z.openapi.samples;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Chat Completion with Custom Headers Example
 * Demonstrates how to use ZaiClient for chat conversations with custom HTTP headers
 */
public class ChatCompletionWithCustomHeadersExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZhipuAiClient client = ZhipuAiClient.builder().build();

        // Create chat request
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(Constants.ModelChatGLM4_5)
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content("Hello, how are you?")
                                .build()
                ))
                .stream(true) // Enable streaming for custom headers support
                .temperature(0.7f)
                .maxTokens(1024)
                .build();

        // Create custom headers
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("X-Custom-User-ID", "user123");
        customHeaders.put("X-Request-Source", "mobile-app");
        customHeaders.put("Session-Id", "session-abc-123");

        try {
            // Execute request with custom headers
            // This works for both streaming and non-streaming requests
            ChatCompletionResponse response = client.chat().createChatCompletion(request, customHeaders);

            // Example for non-streaming request with custom headers
            ChatCompletionCreateParams nonStreamingRequest = ChatCompletionCreateParams.builder()
                    .model(Constants.ModelChatGLM4_5)
                    .messages(Arrays.asList(
                            ChatMessage.builder()
                                    .role(ChatMessageRole.USER.value())
                                    .content("What is artificial intelligence?")
                                    .build()
                    ))
                    .stream(false) // Explicitly set to false for non-streaming
                    .temperature(0.7f)
                    .maxTokens(1024)
                    .build();

            ChatCompletionResponse nonStreamingResponse = client.chat()
                    .createChatCompletion(nonStreamingRequest, customHeaders);

            System.out.println("Non-streaming response: " + nonStreamingResponse.getData());

            if (response.isSuccess() && response.getFlowable() != null) {
                System.out.println("Streaming response with custom headers:");
                response.getFlowable().subscribe(
                    data -> {
                        // Handle streaming chunk
                        if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                            String content = data.getChoices().get(0).getDelta().getContent();
                            if (content != null) {
                                System.out.print(content);
                            }
                        }
                    },
                    error -> System.err.println("\nStream error: " + error.getMessage()),
                    () -> System.out.println("\nStream completed")
                );
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}