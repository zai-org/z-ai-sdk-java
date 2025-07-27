package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.QueryModelResultResponse;

import java.util.Arrays;

/**
 * Chat Completion Example
 * Demonstrates how to use ZaiClient for basic chat conversations
 */
public class ChatAsyncCompletionExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api.key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();
        
        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api.key")
        //         .build();
        
        // Create chat request
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model(Constants.ModelChatGLM4)
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
            ChatCompletionResponse response = client.chat().asyncChatCompletion(request);
            System.out.println("Response Task: " + response.getData());
            Thread.sleep(10000);
            QueryModelResultResponse queryModelResultResponse = client.chat().retrieveAsyncResult(AsyncResultRetrieveParams.builder()
                    .taskId(response.getData().getId()).build());
            System.out.println("Response Data: " + queryModelResultResponse.getData());
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}