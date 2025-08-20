package ai.z.openapi.samples;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.ImageUrl;
import ai.z.openapi.service.model.MessageContent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;

/**
 * Streaming Chat Example
 * Demonstrates how to use ZaiClient for streaming chat conversations
 */
public class ChatCompletionBase64Example {
    
    public static void main(String[] args) throws IOException {
        // Create client
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZhipuAiClient client = ZhipuAiClient.builder().build();

        String file = ClassLoader.getSystemResource("grounding.png").getFile();
        byte[] bytes = Files.readAllBytes(new File(file).toPath());
        Base64.Encoder encoder = Base64.getEncoder();
        String base64 = encoder.encodeToString(bytes);

        // Create chat request
        ChatCompletionCreateParams streamRequest = ChatCompletionCreateParams.builder()
            .model("glm-4.5v")
            .messages(Arrays.asList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(Arrays.asList(
                        MessageContent.builder()
                            .type("image_url")
                            .imageUrl(ImageUrl.builder()
                                .url(base64)
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