package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;

import java.util.Arrays;

/**
 * Chat Completion Example
 * Demonstrates how to use ZaiClient for basic chat conversations
 */
public class ChatCompletionExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api.key
        ZaiClient client = ZaiClient.builder().ofZHIPU().build();

        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api.key.your.api.secret")
        //         .build();

        // Create chat request
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(Constants.ModelChatGLM4_5_AIR)
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content("Hello, how are you?")
                                .build()
                ))
                .stream(false)
                .temperature(0.7f)
                .maxTokens(1024)
                .build();

        try {
            // Execute request
            ChatCompletionResponse response = client.chat().createChatCompletion(request);

            if (response.isSuccess()) {
                Object content = response.getData().getChoices().get(0).getMessage().getContent();
                System.out.println("Response: " + content);
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}