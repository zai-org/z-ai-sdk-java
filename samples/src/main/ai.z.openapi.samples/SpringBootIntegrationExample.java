package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;
import java.util.Arrays;

/**
 * Spring Boot Integration Example
 * Demonstrates how to integrate Z.ai SDK in a Spring Boot application
 * 
 * Note: This is an example class showing the code structure for Spring Boot integration
 * In actual use, you need to add Spring Boot related dependencies and annotations
 */
public class SpringBootIntegrationExample {
    
    /**
     * Simulated Spring Boot Controller
     * In actual use, add @RestController annotation
     */
    public static class AIController {
        
        private final ZaiClient zaiClient;
        
        public AIController() {
            this.zaiClient = ZaiClient.builder()
                .apiKey("your.api.key.your.api.secret")
                .enableTokenCache()
                .build();
        }
        
        /**
         * Chat endpoint
         * In actual use, add @PostMapping("/chat") annotation
         */
        public String chat(ChatRequest request) {
            try {
                ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(Constants.ModelChatGLM4)
                    .messages(Arrays.asList(
                        ChatMessage.builder()
                            .role(ChatMessageRole.USER.value())
                            .content(request.getMessage())
                            .build()
                    ))
                    .build();
                    
                ChatCompletionResponse response = zaiClient.chat().createChatCompletion(params);
                
                if (response.isSuccess()) {
                    String content = response.getData().getChoices().get(0).getMessage().getContent();
                    return content;
                } else {
                    return "Error: " + response.getMsg();
                }
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }
        
        /**
         * Image generation endpoint
         * In actual use, add @PostMapping("/generate-image") annotation
         */
        public String generateImage(ImageRequest request) {
            try {
                ai.z.openapi.service.image.CreateImageRequest imageRequest = 
                    ai.z.openapi.service.image.CreateImageRequest.builder()
                        .model(Constants.ModelCogView3Plus)
                        .prompt(request.getPrompt())
                        .size("1024x1024")
                        .n(1)
                        .build();
                
                ai.z.openapi.service.image.ImageResponse response = 
                    zaiClient.images().generate(imageRequest);
                
                if (response.isSuccess()) {
                    return response.getData().getData().get(0).getUrl();
                } else {
                    return "Error: " + response.getMsg();
                }
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }
    }
    
    /**
     * Chat request model
     */
    public static class ChatRequest {
        private String message;
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    /**
     * Image request model
     */
    public static class ImageRequest {
        private String prompt;
        
        public String getPrompt() {
            return prompt;
        }
        
        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }
    
    /**
     * Configuration class example
     * In actual use, add @Configuration annotation
     */
    public static class ZaiClientConfig {
        
        /**
         * Create ZaiClient Bean
         * In actual use, add @Bean annotation
         */
        public ZaiClient zaiClient() {
            return ZaiClient.builder()
                .enableTokenCache()
                .tokenExpire(3600000) // 1小时
                .build();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Spring Boot Integration Example ===");
        
        // Create controller instance
        AIController controller = new AIController();
        
        // Test chat functionality
        System.out.println("\n=== Testing Chat Functionality ===");
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMessage("Hello, how are you?");
        String chatResponse = controller.chat(chatRequest);
        System.out.println("Chat response: " + chatResponse);
        
        // Test image generation functionality
        System.out.println("\n=== Testing Image Generation Functionality ===");
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setPrompt("A beautiful landscape");
        String imageUrl = controller.generateImage(imageRequest);
        System.out.println("Image URL: " + imageUrl);
        
        System.out.println("\n=== Spring Boot Integration Instructions ===");
        System.out.println("1. Add Spring Boot dependencies to pom.xml");
        System.out.println("2. Add @RestController annotation to Controller class");
        System.out.println("3. Add @PostMapping and other mapping annotations to methods");
        System.out.println("4. Add @Configuration annotation to configuration class");
        System.out.println("5. Add @Bean annotation to Bean methods");
        System.out.println("6. Use @Value annotation to inject configuration properties");
        System.out.println("7. Consider using @Autowired to inject ZaiClient");
    }
}