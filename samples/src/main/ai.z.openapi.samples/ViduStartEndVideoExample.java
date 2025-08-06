package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideosResponse;

import java.util.Arrays;

/**
 * Vidu Image-to-Video Example
 * Demonstrates how to use ZaiClient to generate videos from images using Vidu models
 */
public class ViduStartEndVideoExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();

        generateVideoStartEnd(client);
    }
    
    /**
     * Example of generating video from an image using Vidu
     * Note: You need to provide a valid image file path
     */
    private static void generateVideoStartEnd(ZaiClient client) {
        System.out.println("\n=== Vidu Image-to-Video Generation Example ===");

        try {
            // Create video generation request
            VideoCreateParams request = VideoCreateParams.builder()
                .model(Constants.ModelViduQ1StartEnd) // Using Vidu 2 Image model
                .prompt("An astronaut floating in space with Earth visible in the background, anime style")
                .imageUrl(Arrays.asList("https://aigc-files.bigmodel.cn/api/cogview/20250723213827da171a419b9b4906_0.png", "https://aigc-files.bigmodel.cn/api/cogview/20250723213827da171a419b9b4906_0.png"))
                .duration(5) // 5 seconds duration
                .size("1920x1080")
                    .withAudio(true)
                    .movementAmplitude("auto")
                .build();

            VideosResponse response = client.videos().videoGenerations(request);
            System.out.println("Task " + response.getData());
            
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of checking video generation result
     * Note: You need to replace the taskId with a real task ID from a previous generation request
     */
    private static void checkVideoResult(ZaiClient client, String taskId) {
        System.out.println("\n=== Check Video Generation Result Example ===");
        try {
            VideosResponse response = client.videos().videoGenerationsResult(taskId);

            if (response.isSuccess()) {
                System.out.println("Video generation: " + response.getData());

            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}