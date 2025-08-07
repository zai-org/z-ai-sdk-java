package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideosResponse;

/**
 * CogVideoX Example
 * Demonstrates how to use ZaiClient to generate videos using CogVideoX models
 */
public class CogVideoXExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();
        
        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api_key")
        //         .build();
        
        // Basic Video Generation
        generateBasicVideo(client);
    }
    
    /**
     * Example of basic video generation using CogVideoX
     */
    private static void generateBasicVideo(ZaiClient client) {
        System.out.println("\n=== Basic CogVideoX Generation Example ===");
        
        // Create video generation request
        VideoCreateParams request = VideoCreateParams.builder()
            .model(Constants.ModelCogVideoX) // Using CogVideoX model
            .prompt("A beautiful sunset over the ocean with waves gently crashing on the shore")
            .requestId("cogvideox-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Execute request
            VideosResponse response = client.videos().videoGenerations(request);
            System.out.println("Response: " + response.getData());
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
            // Execute request to check result
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