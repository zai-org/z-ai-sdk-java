package ai.z.openapi.samples;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.ImageUrl;
import ai.z.openapi.service.model.MessageContent;

import java.io.IOException;
import java.util.Arrays;

/**
 * Streaming Chat Example
 * Demonstrates how to use ZaiClient for streaming chat conversations
 */
public class GLM45VExample {
    
    public static void main(String[] args) throws IOException {
        // Create client
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZhipuAiClient client = ZhipuAiClient.builder().build();

        // Create chat request
        ChatCompletionCreateParams streamRequest = ChatCompletionCreateParams.builder()
            .model(Constants.ModelChatGLM4_5V)
            .messages(Arrays.asList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(Arrays.asList(
                        MessageContent.builder()
                            .type("image_url")
                            .imageUrl(ImageUrl.builder()
                                .url("https://cdn.bigmodel.cn/static/logo/register.png")
                                .build())
                            .build(),
                        MessageContent.builder()
                            .type("text")
                            .text("What are the pic talk about?")
                            .build()
                    ))
                    .build()
            ))
            .thinking(ChatThinking.builder().type("enabled").build())
            .build();

        ChatCompletionResponse response = client.chat().createChatCompletion(streamRequest);

        if (response.isSuccess()) {
            Object content = response.getData().getChoices().get(0).getMessage();
            System.out.println("Response: " + content);
        } else {
            System.err.println("Error: " + response.getMsg());
        }
    }
}