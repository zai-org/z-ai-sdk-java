package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.ChatThinkingType;
import ai.z.openapi.service.model.ChatTool;
import ai.z.openapi.service.model.ChatToolType;
import ai.z.openapi.service.model.MCPTool;
import ai.z.openapi.service.model.ResponseFormat;
import ai.z.openapi.service.model.ResponseFormatType;

import java.util.Collections;

/**
 * Chat Completion Example
 * Demonstrates how to use ZaiClient for basic chat conversations
 */
public class ChatCompletionWithMcpServerUrlExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient.builder().ofZHIPU().build()
        ZaiClient client = ZaiClient.builder().ofZAI().build();

        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api_key")
        //         .build();

        // Create chat request
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model("glm-4.7")
            .messages(Collections.singletonList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content("Hello, how to learn english?")
                    .build()
            ))
            .stream(false)
            .thinking(ChatThinking.builder().type(ChatThinkingType.ENABLED.value()).build())
            .responseFormat(ResponseFormat.builder().type(ResponseFormatType.TEXT.value()).build())
            .temperature(1.0f)
            .maxTokens(1024)
            .tools(Collections.singletonList(ChatTool.builder()
                .type(ChatToolType.MCP.value())
                .mcp(MCPTool.builder()
                    .server_url("https://open.bigmodel.cn/api/mcp-broker/proxy/sogou-baike/sse")
                    .server_label("sogou-baike")
                    .transport_type("sse")
                    .headers(Collections.singletonMap("Authorization", "Bearer " + System.getProperty("ZAI_API_KEY")))
                    .build())
                .build()))
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
        } finally {
            client.close();
        }
    }
}