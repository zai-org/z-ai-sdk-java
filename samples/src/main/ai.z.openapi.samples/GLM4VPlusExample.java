package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ImageUrl;
import ai.z.openapi.service.model.MessageContent;

import java.util.Arrays;

public class GLM4VPlusExample {

    public static void main(String[] args) {

        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().apiKey("your.api_key").build();

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-4v-plus-0111")
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content(Arrays.asList(
                                        MessageContent.builder()
                                                .type("text")
                                                .text("What is in this image?")
                                                .build(),
                                        MessageContent.builder()
                                                .type("image_url")
                                                .imageUrl(ImageUrl.builder()
                                                        .url("https://aigc-files.bigmodel.cn/api/cogview/20250723213827da171a419b9b4906_0.png")
                                                        .build())
                                                .build()))
                                .build()
                ))
                .build();

        ChatCompletionResponse response = client.chat().createChatCompletion(request);

        if (response.isSuccess()) {
            Object reply = response.getData().getChoices().get(0).getMessage().getContent();
            System.out.println(reply);
        } else {
            System.err.println("Error: " + response.getMsg());
        }
    }
}
