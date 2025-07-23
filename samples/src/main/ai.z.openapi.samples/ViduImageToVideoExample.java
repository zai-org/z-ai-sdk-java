package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideosResponse;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import org.apache.commons.io.FileUtils;

/**
 * Vidu Image-to-Video Example
 * Demonstrates how to use ZaiClient to generate videos from images using Vidu models
 */
public class ViduImageToVideoExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api.key
        ZaiClient client = ZaiClient.builder().build();
        
        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api.key.your.api.secret")
        //         .build();
        
        // Example: Generate video from image using Vidu
        generateVideoFromImage(client);
        
        // Example: Check video generation result
        checkVideoResult(client);
    }
    
    /**
     * Example of generating video from an image using Vidu
     * Note: You need to provide a valid image file path
     */
    private static void generateVideoFromImage(ZaiClient client) {
        System.out.println("\n=== Vidu Image-to-Video Generation Example ===");
        
        // Path to your image file
        // In a real application, replace with an actual image path
        String imagePath = "path/to/your/image.jpg";
        
        try {
            // For demonstration purposes, we'll skip the actual file reading
            // In a real application, you would read and encode the image file
            String imageBase64 = "sample_base64_string";
            
            // Uncomment the following code to read and encode a real image file
            /*
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                byte[] fileContent = FileUtils.readFileToByteArray(imageFile);
                imageBase64 = Base64.getEncoder().encodeToString(fileContent);
            } else {
                System.err.println("Image file not found: " + imagePath);
                return;
            }
            */
            
            // Create video generation request
            VideoCreateParams request = VideoCreateParams.builder()
                .model(Constants.ModelVidu2Image) // Using Vidu 2 Image model
                .prompt("Transform this image into a dynamic scene with gentle movement")
                .imageUrl(imageBase64) // Base64 encoded image
                .requestId("vidu-image-example-" + System.currentTimeMillis())
                .duration(5) // 5 seconds duration
                .build();
            
            // Skip the actual API call in this example to avoid errors with a fake image
            System.out.println("In a real application, the following code would be executed:");
            System.out.println("VideosResponse response = client.videos().videoGenerations(request);");
            System.out.println("\nNote: This example is skipping the actual API call since we're using a placeholder image.");
            System.out.println("To run this example with a real image, uncomment the image reading code and provide a valid image path.");
            
            // Uncomment the following code to make the actual API call with a real image
            /*
            // Execute request
            VideosResponse response = client.videos().videoGenerations(request);
            
            if (response.isSuccess()) {
                System.out.println("Video generation request successful!");
                System.out.println("Task ID: " + response.getData().getId());
                System.out.println("Status: " + response.getData().getStatus());
                System.out.println("\nNote: Video generation is an asynchronous process.");
                System.out.println("Use the Task ID to check the status and retrieve the result later.");
            } else {
                System.err.println("Error: " + response.getMsg());
            }
            */
            
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