package ai.z.openapi.samples;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.ChatThinkingType;
import ai.z.openapi.service.model.ResponseFormat;
import ai.z.openapi.service.model.ResponseFormatType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Chat Completion Example
 * Demonstrates how to use ZaiClient for basic chat conversations
 */
public class CustomTimeoutExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZhipuAiClient client = ZhipuAiClient.builder()
            .networkConfig(30, 10, 30, 30, TimeUnit.SECONDS)
            .build();

        // Create chat request
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model(Constants.ModelChatGLM4_5)
            .messages(Arrays.asList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content("Hello, how to learn english?")
                    .build()
            ))
            .stream(false)
            .thinking(ChatThinking.builder().type(ChatThinkingType.ENABLED.value()).build())
            .responseFormat(ResponseFormat.builder().type(ResponseFormatType.TEXT.value()).build())
            .temperature(0.7f)
            .maxTokens(1024)
            .build();

        try {
            // Execute request
            ChatCompletionResponse response = client.chat().createChatCompletion(request);

            if (response.isSuccess()) {
                Object content = response.getData().getChoices().get(0).getMessage();
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