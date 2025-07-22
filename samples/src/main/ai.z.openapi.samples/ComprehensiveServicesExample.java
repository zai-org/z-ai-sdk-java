package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.service.embedding.*;
import ai.z.openapi.service.image.*;
import ai.z.openapi.core.Constants;
import java.util.Arrays;

/**
 * Comprehensive Services Example
 * Demonstrates the basic usage of various AI services provided by ZaiClient
 */
public class ComprehensiveServicesExample {
    
    private static final ZaiClient client = ZaiClient.builder().build();
    
    public static void main(String[] args) {
        System.out.println("=== Z.ai SDK Comprehensive Services Example ===");
        
        // 1. Chat Service
        demonstrateChatService();
        
        // 2. Embedding Service
        demonstrateEmbeddingService();
        
        // 3. Image Service
        demonstrateImageService();
        
        // 4. Other Services
        demonstrateOtherServices();
    }
    
    /**
     * Demonstrate chat service
     */
    private static void demonstrateChatService() {
        System.out.println("\n=== 1. Chat Service ===");
        try {
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(Constants.ModelChatGLM4)
                .messages(Arrays.asList(
                    ChatMessage.builder()
                        .role(ChatMessageRole.USER.value())
                        .content("Please briefly introduce artificial intelligence")
                        .build()
                ))
                .maxTokens(100)
                .build();
            
            ChatCompletionResponse response = client.chat().createChatCompletion(request);
            if (response.isSuccess()) {
                System.out.println("✓ Chat service call successful");
                System.out.println("Reply: " + response.getData().getChoices().get(0).getMessage().getContent());
            } else {
                System.out.println("✗ Chat service call failed: " + response.getMsg());
            }
        } catch (Exception e) {
            System.out.println("✗ Chat service exception: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate embedding service
     */
    private static void demonstrateEmbeddingService() {
        System.out.println("\n=== 2. Embedding Service ===");
        try {
            EmbeddingCreateParams request = EmbeddingCreateParams.builder()
                .model(Constants.ModelEmbedding3)
                .input(Arrays.asList("artificial intelligence", "machine learning"))
                .build();
            
            EmbeddingResponse response = client.embeddings().create(request);
            if (response.isSuccess()) {
                System.out.println("✓ Embedding service call successful");
                System.out.println("Generated " + response.getData().getData().size() + " embedding vectors");
                System.out.println("Vector dimensions: " + response.getData().getData().get(0).getEmbedding().size());
            } else {
                System.out.println("✗ Embedding service call failed: " + response.getMsg());
            }
        } catch (Exception e) {
            System.out.println("✗ Embedding service exception: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate image service
     */
    private static void demonstrateImageService() {
        System.out.println("\n=== 3. Image Service ===");
        try {
            CreateImageRequest request = CreateImageRequest.builder()
                .model(Constants.ModelCogView3Plus)
                .prompt("A simple geometric pattern")
                .size("512x512")
                .n(1)
                .build();
            
            ImageResponse response = client.images().generate(request);
            if (response.isSuccess()) {
                System.out.println("✓ Image service call successful");
                System.out.println("Generated image URL: " + response.getData().getData().get(0).getUrl());
            } else {
                System.out.println("✗ Image service call failed: " + response.getMsg());
            }
        } catch (Exception e) {
            System.out.println("✗ Image service exception: " + e.getMessage());
        }
    }
    
    /**
     * Other available services
     */
    private static void demonstrateOtherServices() {
        System.out.println("\n=== 4. Other Available Services ===");
        System.out.println("The following services are accessible through ZaiClient (refer to documentation for specific APIs):");
        System.out.println("• Audio Service: client.audio() - Speech synthesis and recognition");
        System.out.println("• Files Service: client.files() - File management and processing");
        System.out.println("• Assistants Service: client.assistants() - AI assistant management");
        System.out.println("• Agents Service: client.agents() - Agent interactions");
        System.out.println("• Knowledge Service: client.knowledge() - Knowledge base operations");
        System.out.println("• Batch Service: client.batch() - Batch operations");
        System.out.println("• Web Search Service: client.webSearch() - Web search integration");
        System.out.println("• Videos Service: client.videos() - Video processing");
        
        System.out.println("\n=== Supported Model Types ===");
        System.out.println("• Text Generation: GLM-4 series, GLM-Z1 series, CodeGeeX-4, etc.");
        System.out.println("• Visual Understanding: GLM-4V series");
        System.out.println("• Image Generation: CogView series");
        System.out.println("• Video Generation: CogVideoX series, Vidu series");
        System.out.println("• Speech Recognition: GLM-ASR");
        System.out.println("• Real-time Interaction: GLM-Realtime series");
        System.out.println("• Embedding Vectors: Embedding-3");
        System.out.println("• Specialized Models: CharGLM-3, CogTTS, Rerank, etc.");
    }
}