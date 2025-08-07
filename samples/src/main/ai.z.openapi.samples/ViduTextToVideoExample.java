package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideosResponse;

/**
 * Vidu Text-to-Video Example
 * Demonstrates how to use ZaiClient to generate videos from text using Vidu models
 */
public class ViduTextToVideoExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();
        
        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api_key")
        //         .build();
        
        // Example: Generate video from text using Vidu
        generateVideoFromText(client);
    }
    
    /**
     * Example of generating video from text using Vidu
     */
    private static void generateVideoFromText(ZaiClient client) {
        System.out.println("\n=== Vidu Text-to-Video Generation Example ===");
        
        // Create video generation request
        VideoCreateParams request = VideoCreateParams.builder()
            .model(Constants.ModelViduQ1Text) // Using Vidu Q1 Text model
            .prompt("A person walking through a beautiful forest with sunlight filtering through the trees")
            .requestId("vidu-text-example-" + System.currentTimeMillis())
                .duration(5)
                .size("1920x1080")
            .build();
        
        try {
            // Execute request
            VideosResponse response = client.videos().videoGenerations(request);
            System.out.println("Data: " + response.getData());
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of generating video from text with custom settings
     */
    private static void generateVideoFromTextWithCustomSettings(ZaiClient client) {
        System.out.println("\n=== Vidu Text-to-Video with Custom Settings Example ===");
        
        // Create video generation request with custom settings
        VideoCreateParams request = VideoCreateParams.builder()
            .model(Constants.ModelViduQ1Text) // Using Vidu Q1 Text model
            .prompt("An astronaut floating in space with Earth visible in the background, anime style")
            .requestId("vidu-text-custom-example-" + System.currentTimeMillis())
            .quality("high") // High quality setting
            .withAudio(true) // Generate with audio
                .duration(5)
                .size("1920x1080")
            .fps(30) // 30 frames per second
            .build();
        
        try {
            // Execute request
            VideosResponse response = client.videos().videoGenerations(request);
            
            if (response.isSuccess()) {
                System.out.println("Custom video generation request successful!");
                System.out.println("Task ID: " + response.getData().getId());
                System.out.println("Data: " + response.getData());
                System.out.println("\nNote: Video generation is an asynchronous process.");
                System.out.println("Use the Task ID to check the status and retrieve the result later.");
            } else {
                System.err.println("Error: " + response.getMsg());
            }
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
            // Skip the actual API call in this example to avoid errors with a fake task ID
            if (!taskId.equals("your-task-id-here")) {
                // Execute request to check result
                VideosResponse response = client.videos().videoGenerationsResult(taskId);
                
                if (response.isSuccess()) {
                    System.out.println("Video generation: " + response.getData());
                } else {
                    System.err.println("Error: " + response.getMsg());
                }
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}