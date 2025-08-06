package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.tools.ChoiceDelta;
import ai.z.openapi.service.tools.SearchChatMessage;
import ai.z.openapi.service.tools.WebSearchApiResponse;
import ai.z.openapi.service.tools.WebSearchMessage;
import ai.z.openapi.service.tools.WebSearchParamsRequest;
import ai.z.openapi.service.web_search.WebSearchRequest;
import ai.z.openapi.service.web_search.WebSearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Web Search Example
 * Demonstrates how to use ZaiClient for web search capabilities
 */
public class WebSearchExample {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZaiClient client = ZaiClient.builder().build();
        
        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api_key")
        //         .build();
        
        // Example 1: Basic Web Search
        basicWebSearch(client);
    }
    
    /**
     * Example of basic web search functionality
     */
    private static void basicWebSearch(ZaiClient client) {
        System.out.println("\n=== Basic Web Search Example ===");
        
        // Create web search request
        WebSearchRequest request = WebSearchRequest.builder()
            .searchEngine("search_std")
            .searchQuery("latest AI technology trends")
            .count(3) // Number of results to return
            .searchRecencyFilter("oneYear") // Filter for results within the last year
            .contentSize("high") // Request detailed content
            .requestId("web-search-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Execute request
            WebSearchResponse response = client.webSearch().createWebSearch(request);
            
            if (response.isSuccess()) {
                System.out.println("Search successful!");
                System.out.println("Number of results: " + response.getData().getWebSearchResp().size());
                
                // Display search results
                response.getData().getWebSearchResp().forEach(result -> {
                    System.out.println("\nTitle: " + result.getTitle());
                    System.out.println("Result: " + result);
                });
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of web search pro functionality (non-streaming)
     */
    private static void webSearchPro(ZaiClient client) {
        System.out.println("\n=== Web Search Pro Example ===");
        
        // Create messages for the search
        List<SearchChatMessage> messages = new ArrayList<>();
        SearchChatMessage userMessage = new SearchChatMessage(
            ChatMessageRole.USER.value(),
            "What are the latest developments in quantum computing?"
        );
        messages.add(userMessage);
        
        // Create web search pro request
        WebSearchParamsRequest request = WebSearchParamsRequest.builder()
            .model("web-search-pro")
            .stream(false) // Non-streaming mode
            .messages(messages)
            .requestId("web-search-pro-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Execute request
            WebSearchApiResponse response = client.webSearch().createWebSearchPro(request);
            
            if (response.isSuccess()) {
                System.out.println("Search successful!");
                
                // Display search result
                WebSearchMessage content = response.getData().getChoices().get(0).getMessage();
                System.out.println("\nResponse: " + content);
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of web search pro with streaming functionality
     */
    private static void webSearchProStream(ZaiClient client) {
        System.out.println("\n=== Web Search Pro Streaming Example ===");
        
        // Create messages for the search
        List<SearchChatMessage> messages = new ArrayList<>();
        SearchChatMessage userMessage = new SearchChatMessage(
            ChatMessageRole.USER.value(),
            "What are the recent advancements in renewable energy?"
        );
        messages.add(userMessage);
        
        // Create web search pro streaming request
        WebSearchParamsRequest request = WebSearchParamsRequest.builder()
            .model("web-search-pro")
            .stream(true) // Enable streaming
            .messages(messages)
            .requestId("web-search-pro-stream-example-" + System.currentTimeMillis())
            .build();
        
        try {
            // Execute streaming request
            WebSearchApiResponse response = client.webSearch().createWebSearchProStream(request);
            
            if (response.isSuccess() && response.getFlowable() != null) {
                System.out.println("Streaming search started...");
                
                // Track streaming progress
                AtomicInteger messageCount = new AtomicInteger(0);
                AtomicBoolean isFirst = new AtomicBoolean(true);
                StringBuilder fullContent = new StringBuilder();
                
                // Subscribe to the stream
                response.getFlowable().doOnNext(webSearchPro -> {
                    if (isFirst.getAndSet(false)) {
                        System.out.println("Receiving stream response:");
                    }
                    
                    if (webSearchPro.getChoices() != null && !webSearchPro.getChoices().isEmpty()) {
                        ChoiceDelta content = webSearchPro.getChoices().get(0).getDelta();
                        System.out.print(content);
                        fullContent.append(content);
                        messageCount.incrementAndGet();
                    }
                })
                .doOnComplete(() -> {
                    System.out.println("\n\nStream completed. Received " + messageCount.get() + " chunks.");
                    System.out.println("Full response length: " + fullContent.length() + " characters");
                })
                .blockingSubscribe();
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}