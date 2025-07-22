package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.image.*;
import ai.z.openapi.core.Constants;

/**
 * Image Generation Example
 * Demonstrates how to use ZaiClient to generate images
 */
public class ImageGenerationExample {
    
    public static void main(String[] args) {
        // Create client
        ZaiClient client = ZaiClient.builder().build();
        
        // Create image generation request
        CreateImageRequest request = CreateImageRequest.builder()
            .model(Constants.ModelCogView3Plus)
            .prompt("A beautiful sunset over mountains, digital art style")
            .size("1024x1024")
            .build();
        
        try {
            // Execute request
            ImageResponse response = client.images().createImage(request);
            
            if (response.isSuccess()) {
                System.out.println("Successfully generated image:");
                System.out.println("Creation time: " + response.getData().getCreated());
                
                response.getData().getData().forEach(image -> {
                    System.out.println("\nImage URL: " + image.getUrl());
                    if (image.getRevisedPrompt() != null) {
                        System.out.println("Revised prompt: " + image.getRevisedPrompt());
                    }
                });
                
                System.out.println("\nTip: Please copy the URL above to your browser to view the generated image");
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}