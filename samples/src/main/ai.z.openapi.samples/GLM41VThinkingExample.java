package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ImageUrl;
import ai.z.openapi.service.model.MessageContent;

import java.util.Arrays;

public class GLM41VThinkingExample {

    public static void main(String[] args) {
        String apiKey = ""; // 请填写您自己的APIKey
        ZaiClient client = ZaiClient.builder().ofZHIPU()
                .apiKey(apiKey)
                .build();

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-4.1v-thinking-flashx")
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content(Arrays.asList(
                                        MessageContent.builder()
                                                .type("text")
                                                .text("描述下这张图片")
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
            System.err.println("错误: " + response.getMsg());
        }
    }

}
