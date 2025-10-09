package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.moderations.*;

import java.util.Arrays;
import java.util.List;

/**
 * Moderation Example
 * Demonstrates how to use ZaiClient to moderate content for safety
 */
public class ModerationExample {
    
    public static void main(String[] args) {
        // Create client
        ZaiClient client = ZaiClient.builder().build();
        
        // Example 1: Text moderation
        System.out.println("=== Text Moderation Example ===");
        moderateText(client);
        
        // Example 2: Image moderation
        System.out.println("\n=== Image Moderation Example ===");
        moderateImage(client);
        
        // Example 3: Mixed content moderation
        System.out.println("\n=== Mixed Content Moderation Example ===");
        moderateMixedContent(client);
    }
    
    private static void moderateText(ZaiClient client) {
        // Create text moderation inputs
        List<ModerationInput> inputs = Arrays.asList(
            ModerationInput.text("This is a normal message about technology."),
            ModerationInput.text("Hello, how are you today?"),
            ModerationInput.text("Let's discuss artificial intelligence and machine learning.")
        );
        
        // Create moderation request
        ModerationCreateParams request = ModerationCreateParams.builder()
            .model("glm-4-content-safety")
            .input(inputs)
            .build();
        
        try {
            // Execute request
            ModerationResponse response = client.moderations().createModeration(request);
            
            if (response.isSuccess()) {
                System.out.println("Text moderation completed successfully:");
                System.out.println("Request ID: " + response.getData().getRequestId());
                System.out.println("Processing time: " + response.getData().getProcessedTime() + "ms");
                
                response.getData().getResultList().forEach(item -> {
                    System.out.println("\nContent Type: " + item.getContentType());
                    System.out.println("Risk Level: " + item.getRiskLevel());
                    System.out.println("Risk Type: " + item.getRiskType());
                    System.out.println("Is Safe: " + item.isSafe());
                    System.out.println("Is Flagged: " + item.isFlagged());
                    if (item.getConfidence() != null) {
                        System.out.println("Confidence: " + String.format("%.2f", item.getConfidence()));
                    }
                });
            } else {
                System.err.println("Error: Text moderation failed: " + response.getMsg());
                if (response.getError() != null) {
                    System.err.println("Error details: " + response.getError().getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Text moderation exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void moderateImage(ZaiClient client) {
        // Create image moderation input
        List<ModerationInput> inputs = Arrays.asList(
            ModerationInput.image("https://example.com/sample-image.jpg"),
            ModerationInput.image("https://example.com/another-image.png")
        );
        
        // Create moderation request
        ModerationCreateParams request = ModerationCreateParams.builder()
            .model("glm-4-content-safety")
            .input(inputs)
            .build();
        
        try {
            // Execute request
            ModerationResponse response = client.moderations().createModeration(request);
            
            if (response.isSuccess()) {
                System.out.println("Image moderation completed successfully:");
                System.out.println("Request ID: " + response.getData().getRequestId());
                
                response.getData().getResultList().forEach(item -> {
                    System.out.println("\nContent Type: " + item.getContentType());
                    System.out.println("Risk Level: " + item.getRiskLevel());
                    System.out.println("Status: " + (item.isSafe() ? "SAFE" : "FLAGGED"));
                    if (item.getRiskType() != null) {
                        System.out.println("Risk Type: " + item.getRiskType());
                    }
                });
            } else {
                System.err.println("Error: Image moderation failed: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Image moderation exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void moderateMixedContent(ZaiClient client) {
        // Create mixed content moderation inputs
        List<ModerationInput> inputs = Arrays.asList(
            ModerationInput.text("This is a text message to be moderated."),
            ModerationInput.image("https://example.com/image-to-check.jpg"),
            ModerationInput.video("https://example.com/video-sample.mp4"),
            ModerationInput.audio("https://example.com/audio-sample.mp3")
        );
        
        // Create moderation request
        ModerationCreateParams request = ModerationCreateParams.builder()
            .model("glm-4-content-safety")
            .input(inputs)
            .build();
        
        try {
            // Execute request
            ModerationResponse response = client.moderations().createModeration(request);
            
            if (response.isSuccess()) {
                System.out.println("Mixed content moderation completed successfully:");
                System.out.println("Total items processed: " + response.getData().getResultList().size());
                
                response.getData().getResultList().forEach(item -> {
                    System.out.println("\n--- Moderation Result ---");
                    System.out.println("Content Type: " + item.getContentType());
                    System.out.println("Risk Assessment: " + item.getRiskLevel());
                    System.out.println("Safety Status: " + (item.isSafe() ? "✓ SAFE" : "⚠ FLAGGED"));
                    
                    if (item.isFlagged()) {
                        System.out.println("Risk Category: " + item.getRiskType());
                        if (item.getDetails() != null) {
                            System.out.println("Details: " + item.getDetails());
                        }
                    }
                });
                
                // Summary statistics
                long safeCount = response.getData().getResultList().stream()
                    .mapToLong(item -> item.isSafe() ? 1 : 0)
                    .sum();
                long flaggedCount = response.getData().getResultList().size() - safeCount;
                
                System.out.println("\n--- Summary ---");
                System.out.println("Safe items: " + safeCount);
                System.out.println("Flagged items: " + flaggedCount);
                
                if (response.getData().getUsage() != null) {
                    System.out.println("Total tokens used: " + response.getData().getUsage().getTotalTokens());
                }
            } else {
                System.err.println("Error: Mixed content moderation failed: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Mixed content moderation exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}