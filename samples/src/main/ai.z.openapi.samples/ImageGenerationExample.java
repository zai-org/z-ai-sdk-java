package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.image.CreateImageRequest;
import ai.z.openapi.service.image.ImageResponse;

/**
 * Image Generation Example
 * Demonstrates how to use ZaiClient to generate images
 */
public class ImageGenerationExample {
    
    public static void main(String[] args) {
        // Create client
        ZaiClient client = ZaiClient.builder().ofZHIPU().build();
        
        // Create image generation request
        CreateImageRequest request = CreateImageRequest.builder()
            .model(Constants.ModelCogView4250304)
            .prompt("A beautiful sunset over mountains, digital art style")
            .size("1024x1024")
            .build();
        ImageResponse response = client.images().createImage(request);
        System.out.println(response.getData());
    }
}