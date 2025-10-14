package ai.z.openapi.samples;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.ChatThinkingType;
import ai.z.openapi.service.model.Delta;
import ai.z.openapi.service.model.ResponseFormat;
import ai.z.openapi.service.model.ResponseFormatType;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Chat Completion Example
 * Demonstrates how to use ZaiClient for basic chat conversations
 */
public class CustomClientExample {

    public static void main(String[] args) throws Exception {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiConfig zaiConfig = ZaiConfig.builder()
            .apiKey(System.getenv("ZAI_API_KEY"))
            .baseUrl(Constants.ZHIPU_AI_BASE_URL)
            .customHeaders(Collections.emptyMap())
            .disableTokenCache(true)
            .requestTimeOut(600)
            .timeOutTimeUnit(TimeUnit.SECONDS)
            .connectTimeout(60)
            .connectionPoolKeepAliveDuration(10)
            .connectionPoolTimeUnit(TimeUnit.SECONDS)
            .connectionPoolMaxIdleConnections(20)
            .build();

        ZhipuAiClient client = new ZhipuAiClient(zaiConfig);

        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api_key")
        //         .build();

        // Create chat request
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model("glm-4.6")
            .messages(Arrays.asList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content("Hello, who are you?")
                    .build()
            ))
            .stream(true)
            .thinking(ChatThinking.builder().type(ChatThinkingType.ENABLED.value()).build())
            .responseFormat(ResponseFormat.builder().type(ResponseFormatType.TEXT.value()).build())
            .temperature(0.7f)
            .maxTokens(1024)
            .build();

        try {
            // Execute request
            ChatCompletionResponse response = client.chat().createChatCompletion(request);

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
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
          client.close();
        }
    }
}