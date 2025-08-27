package ai.z.openapi.samples;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import java.util.Arrays;

public class ChatCompletionMultiFileExample {

    public static void main(String[] args) {
        ZhipuAiClient client = ZhipuAiClient.builder()
            .build();

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model("glm-4.5v")
            .messages(Arrays.asList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(Arrays.asList(
                        MessageContent.builder()
                            .type("file_url")
                            .fileUrl(FileUrl.builder()
                                .url("https://cdn.bigmodel.cn/static/demo/demo2.txt")
                                .build())
                            .build(),
                        MessageContent.builder()
                            .type("file_url")
                            .fileUrl(FileUrl.builder()
                                .url("https://cdn.bigmodel.cn/static/demo/demo1.pdf")
                                .build())
                            .build(),
                        MessageContent.builder()
                            .type("text")
                            .text("What are the files show about?")
                            .build()
                    ))
                    .build()
            ))
            .thinking(ChatThinking.builder()
                .type("enabled")
                .build())
            .build();

        ChatCompletionResponse response = client.chat().createChatCompletion(request);

        if (response.isSuccess()) {
            Object reply = response.getData().getChoices().get(0).getMessage();
            System.out.println(reply);
        } else {
            System.err.println("Error: " + response.getMsg());
        }
    }
}