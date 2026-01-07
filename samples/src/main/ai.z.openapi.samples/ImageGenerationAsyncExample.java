package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.image.AsyncImageResponse;
import ai.z.openapi.service.image.CreateImageRequest;

/**
 * Images Example
 * Demonstrates how to use ZaiClient to generate async image
 */
public class ImageGenerationAsyncExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient.builder().ofZHIPU().build()
        ZaiClient client = ZaiClient.builder().baseUrl("https://dev.bigmodel.cn/api/paas/v4/").apiKey("08d9c717e10a4dbc8bd3963cb70aafe0.ZAMAdBKwQsrlAbcZ").build();

        generateAsyncImage(client);
        client.close();
    }

    private static void generateAsyncImage(ZaiClient client) {
        System.out.println("\n=== Basic Images Generation Example ===");

        // Create image generation request
        CreateImageRequest request = CreateImageRequest.builder()
            .model("glm-image")
            .prompt("A beautiful sunset over mountains, digital art style")
            .size("1024x1024")
            .quality("hd")
            .build();
        
        try {
            // Execute request
            AsyncImageResponse response = client.images().createImageAsync(request);
            System.out.println("Response 1: " + response.getData());
            String taskId = response.getData().getId();
            response = client.images().queryAsyncResult(taskId);
            System.out.println("Response 2: " + response.getData());
            Thread.sleep(40000);
            response = client.images().queryAsyncResult(taskId);
            System.out.println("Response 3: " + response.getData());
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}