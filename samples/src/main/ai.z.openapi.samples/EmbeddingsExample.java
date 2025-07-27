package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.embedding.*;
import ai.z.openapi.core.Constants;
import java.util.Arrays;

/**
 * Embeddings Example
 * Demonstrates how to use ZaiClient to generate text embeddings
 */
public class EmbeddingsExample {
    
    public static void main(String[] args) {
        // Create client

        ZaiClient client = ZaiClient.builder().build();
        
        // Create embedding request
        EmbeddingCreateParams request = EmbeddingCreateParams.builder()
            .model(Constants.ModelEmbedding3)
            .input(Arrays.asList("Hello world", "How are you?", "How is the weather today?"))
            .build();
        
        try {
            // Execute request
            EmbeddingResponse response = client.embeddings().createEmbeddings(request);
            
            if (response.isSuccess()) {
                System.out.println("Successfully generated embeddings:");
                System.out.println("Model: " + response.getData().getModel());
                System.out.println("Usage statistics: " + response.getData().getUsage().getTotalTokens() + " tokens");
                
                response.getData().getData().forEach(embedding -> {
                    System.out.println("\nIndex: " + embedding.getIndex());
                    System.out.println("Vector dimensions: " + embedding.getEmbedding().size());
                    System.out.println("Vector first 5 values: " + 
                        embedding.getEmbedding().subList(0, Math.min(5, embedding.getEmbedding().size())));
                });
            } else {
                System.err.println("Error: Embedding generation failed: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Embedding exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}