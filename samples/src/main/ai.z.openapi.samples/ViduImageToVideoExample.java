package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideosResponse;

/**
 * Vidu Image-to-Video Example
 * Demonstrates how to use ZaiClient to generate videos from images using Vidu models
 */
public class ViduImageToVideoExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();

        generateVideoFromImage(client);
    }
    
    /**
     * Example of generating video from an image using Vidu
     * Note: You need to provide a valid image file path
     */
    private static void generateVideoFromImage(ZaiClient client) {
        System.out.println("\n=== Vidu Image-to-Video Generation Example ===");
        
        // Path to your image file
        // In a real application, replace with an actual image path

        
        try {
            // Create video generation request
            VideoCreateParams request = VideoCreateParams.builder()
                .model(Constants.ModelVidu2Image) // Using Vidu 2 Image model
                .prompt("Transform this image into a dynamic scene with gentle movement")
                .imageUrl("https://aigc-files.bigmodel.cn/api/cogview/20250723213827da171a419b9b4906_0.png") // Base64 encoded image
                .duration(4) // 5 seconds duration
                .size("1280x720")
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
    private static void checkVideoResult(ZaiClient client) {
        System.out.println("\n=== Check Video Generation Result Example ===");
        
        // Replace with a real task ID from a previous generation request
        String taskId = "your-task-id-here";
        
        System.out.println("Checking result for task ID: " + taskId);
        System.out.println("Note: In a real application, replace 'your-task-id-here' with an actual task ID.");
        
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