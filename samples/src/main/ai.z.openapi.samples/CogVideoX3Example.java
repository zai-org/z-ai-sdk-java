package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideoObject;
import ai.z.openapi.service.videos.VideosResponse;

import java.util.Arrays;

/**
 * CogVideoX-3 Example
 * Demonstrates how to use ZaiClient for advanced video generation with CogVideoX-3
 * Features: Text-to-video, Image-to-video, First-last frame generation
 */
public class CogVideoX3Example {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();

        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api_key")
        //         .build();

        // Video generation examples
        textToVideoExample(client);
        imageToVideoExample(client);
        firstLastFrameVideoExample(client);
    }
    
    /**
     * Example of text-to-video generation using CogVideoX-3
     */
    private static void textToVideoExample(ZaiClient client) {
        System.out.println("=== CogVideoX-3 Text-to-Video Generation Example ===");
        
        try {
            VideoCreateParams request = VideoCreateParams.builder()
                .model("cogvideox-3")
                .prompt("A cute kitten chasing butterflies in a garden, bright sunshine, blooming flowers, clear and stable picture")
                .quality("quality")  // "quality" for quality priority, "speed" for speed priority
                .withAudio(true)
                .size("1920x1080")  // Video resolution, supports up to 4K
                .fps(30)  // Frame rate, can be 30 or 60
                .build();
            
            VideosResponse response = client.videos().videoGenerations(request);
            
            if (response.isSuccess()) {
                String taskId = response.getData().getId();
                System.out.println("Video generation task submitted, Task ID: " + taskId);
                System.out.println("Please wait for video generation to complete...");
                
                // Wait and check result
                Thread.sleep(60000); // Wait 1 minute
                checkVideoResult(client, taskId);
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of image-to-video generation using CogVideoX-3
     */
    private static void imageToVideoExample(ZaiClient client) {
        System.out.println("\n=== CogVideoX-3 Image-to-Video Generation Example ===");
        
        try {
            VideoCreateParams request = VideoCreateParams.builder()
                .model("cogvideox-3")
                .imageUrl("https://img.iplaysoft.com/wp-content/uploads/2019/free-images/free_stock_photo.jpg")
                .prompt("Make the scene come alive, showing natural dynamic effects")
                .quality("quality")
                .withAudio(true)
                .size("1920x1080")
                .fps(30)
                .build();

            VideosResponse response = client.videos().videoGenerations(request);
            
            if (response.isSuccess()) {
                String taskId = response.getData().getId();
                System.out.println("Image-to-video task submitted, Task ID: " + taskId);
                System.out.println("Please wait for video generation to complete...");
                
                // Wait and check result
                Thread.sleep(60000); // Wait 1 minute
                checkVideoResult(client, taskId);
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of first-last frame video generation using CogVideoX-3
     * This is a new feature in CogVideoX-3
     */
    private static void firstLastFrameVideoExample(ZaiClient client) {
        System.out.println("\n=== CogVideoX-3 First-Last Frame Video Generation Example ===");
        
        try {
            // Define first and last frame URLs
            String firstFrameUrl = "https://gd-hbimg.huaban.com/ccee58d77afe8f5e17a572246b1994f7e027657fe9e6-qD66In_fw1200webp";
            String lastFrameUrl = "https://gd-hbimg.huaban.com/cc2601d568a72d18d90b2cc7f1065b16b2d693f7fa3f7-hDAwNq_fw1200webp";

            VideoCreateParams request = VideoCreateParams.builder()
                .model("cogvideox-3")
                .imageUrl(Arrays.asList(firstFrameUrl, lastFrameUrl))  // Pass first and last frame URLs
                .prompt("Dragon King transforms into Ao Bing, ink wash style rendering, main subject slowly transforms with rotating camera movement, smooth and natural transition")
                .quality("quality")
                .withAudio(true)
                .size("1920x1080")
                .fps(30)
                .build();

            VideosResponse response = client.videos().videoGenerations(request);
            
            if (response.isSuccess()) {
                String taskId = response.getData().getId();
                System.out.println("First-last frame video generation task submitted, Task ID: " + taskId);
                System.out.println("Please wait for video generation to complete...");
                System.out.println("Note: First-last frame generation can create coherent transition videos, naturally connecting static frames into dynamic narratives.");
                
                // Wait and check result
                Thread.sleep(60000); // Wait 1 minute
                checkVideoResult(client, taskId);
            } else {
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check video generation result
     */
    private static void checkVideoResult(ZaiClient client, String taskId) {
        try {
            VideosResponse response = client.videos().videoGenerationsResult(taskId);
            
            if (response.isSuccess()) {
                VideoObject result = response.getData();
                String status = result.getTaskStatus();
                
                System.out.println("Task status: " + status);
                
                if ("SUCCESS".equals(status)) {
                    System.out.println("Video generation successful!");
                    if (result.getVideoResult() != null && !result.getVideoResult().isEmpty()) {
                        System.out.println("Video URL: " + result.getVideoResult().get(0).getUrl());
                    }
                } else if ("PROCESSING".equals(status)) {
                    System.out.println("Video is still being generated, please check again later...");
                }
            } else {
                System.err.println("Query result error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while querying video result: " + e.getMessage());
            e.printStackTrace();
        }
    }
}