package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import java.util.concurrent.TimeUnit;

/**
 * Client Configuration Example
 * Demonstrates different configuration methods for ZaiClient
 */
public class ClientConfigurationExample {
    
    public static void main(String[] args) {
    
        System.out.println("=== Basic Configuration Example ===");
        ZaiClient basicClient = ZaiClient.builder().build();
        System.out.println("✓ Basic client created successfully");
        
        // Complete configuration example
        System.out.println("\n=== Complete Configuration Example ===");
        ZaiClient advancedClient = ZaiClient.builder()
            .apiKey("your.api.key")
            .baseUrl("https://api.z.ai/api/paas/v4/")
            .enableTokenCache()
            .tokenExpire(3600000) // 1 hour
            .connectionPool(10, 5, TimeUnit.MINUTES)
            .build();
        System.out.println("✓ Advanced client created successfully");
        
        // ZHIPU platform specific client
        System.out.println("\n=== ZHIPU Platform Specific Configuration ===");
        ZaiClient zhipuClient = ZaiClient.ofZHIPU("your.api.key.your.api.secret").build();
        System.out.println("✓ ZHIPU platform client created successfully");
        
        // Custom configuration example
        System.out.println("\n=== Custom Configuration Example ===");
        ZaiClient customClient = ZaiClient.builder()
            .apiKey("your.api.key")
            .baseUrl("https://custom.api.endpoint/")
            .enableTokenCache()
            .tokenExpire(7200000)
            .connectionPool(20, 10, TimeUnit.MINUTES) 
            .build();
        System.out.println("✓ Custom client created successfully");
        
        System.out.println("\n=== Configuration Description ===");
        System.out.println("1. apiKey: API key, format is 'key.secret'");
        System.out.println("2. baseUrl: API base URL");
        System.out.println("3. enableTokenCache: Enable token caching to improve performance");
        System.out.println("4. tokenExpire: Token expiration time (milliseconds)");
        System.out.println("5. connectionPool: Connection pool configuration (max connections, keep-alive time, time unit)");
        System.out.println("\nRecommended to use environment variable ZAI_API_KEY to set API key for better security");
    }
}