package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;
import java.util.Arrays;
import java.util.Map;

/**
 * Function Calling Example
 * Demonstrates how to use ZaiClient for function calling
 */
public class FunctionCallingExample {
    
    public static void main(String[] args) {
        // Create client
        ZaiClient client = ZaiClient.builder().build();
        
        // Define function
        ChatTool weatherTool = ChatTool.builder()
            .type(ChatToolType.FUNCTION.value())
            .function(ChatFunction.builder()
                .name("get_weather")
                .description("Get current weather for a location")
                .parameters(ChatFunctionParameters.builder()
                    .type("object")
                    .properties(Map.of(
                        "location", Map.of(
                            "type", "string",
                            "description", "City name"
                        )
                    ))
                    .required(Arrays.asList("location"))
                    .build())
                .build())
            .build();
        
        // Create request with functions
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
            .model(Constants.ModelChatGLM4)
            .messages(Arrays.asList(
                ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content("What's the weather like in Beijing?")
                    .build()
            ))
            .tools(Arrays.asList(weatherTool))
            .toolChoice("auto")
            .build();
        
        try {
            // Execute request
            ChatCompletionResponse response = client.chat().createChatCompletion(request);
            
            if (response.isSuccess()) {
                ChatCompletionChoice choice = response.getData().getChoices().get(0);
                
                // Check if there are function calls
                if (choice.getMessage().getToolCalls() != null && !choice.getMessage().getToolCalls().isEmpty()) {
                    System.out.println("AI requests to call function:");
                    for (ChatToolCall toolCall : choice.getMessage().getToolCalls()) {
                        System.out.println("Function name: " + toolCall.getFunction().getName());
                        System.out.println("Arguments: " + toolCall.getFunction().getArguments());
                        
                        // Here you should actually execute the function and return results
                        // For demonstration, we simulate a weather result
                        String weatherResult = "{\"temperature\": \"22°C\", \"condition\": \"Sunny\", \"humidity\": \"65%\"}";
                        
                        System.out.println("Simulated function execution result: " + weatherResult);
                    }
                } else {
                    String content = choice.getMessage().getContent();
                    System.out.println("AI direct answer: " + content);
                }
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}