package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import java.util.Arrays;

public class ChatCompletionMultiFileExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient.builder().ofZHIPU().build()
        ZaiClient client = ZaiClient.builder()
            .build();

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model("glm-4.6v")
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
        client.close();
    }
}