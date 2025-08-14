package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import ai.z.openapi.core.Constants;
import java.util.*;

public class FunctionCallingExample {

    // Simulate weather API
    public static Map<String, Object> getWeather(String location, String date) {
        Map<String, Object> weather = new HashMap<>();
        weather.put("location", location);
        weather.put("date", date != null ? date : "today");
        weather.put("weather", "sunny");
        weather.put("temperature", "25°C");
        weather.put("humidity", "60%");
        return weather;
    }

    // Simulate stock API
    public static Map<String, Object> getStockPrice(String symbol) {
        Map<String, Object> stock = new HashMap<>();
        stock.put("symbol", symbol);
        stock.put("price", 150.25);
        stock.put("change", "+2.5%");
        return stock;
    }

    public static void main(String[] args) {
        ZaiClient client = ZaiClient.builder().build();

        // Define function tools
        Map<String, ChatFunctionParameterProperty> properties = new HashMap<>();
        ChatFunctionParameterProperty locationProperty = ChatFunctionParameterProperty
                .builder().type("string").description("City name, for example: Beijing").build();
        properties.put("location", locationProperty);
        ChatFunctionParameterProperty unitProperty = ChatFunctionParameterProperty
                .builder().type("string").enums(Arrays.asList("celsius", "fahrenheit")).build();
        properties.put("unit", unitProperty);
        ChatTool weatherTool = ChatTool.builder()
                .type(ChatToolType.FUNCTION.value())
                .function(ChatFunction.builder()
                        .name("get_weather")
                        .description("Get weather information for a specified location")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(properties)
                                .required(Collections.singletonList("location"))
                                .build())
                        .build())
                .build();

        // Create request
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(Constants.ModelChatGLM4_5)
                .messages(Collections.singletonList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content("How's the weather in Beijing today?")
                                .build()
                ))
                .tools(Collections.singletonList(weatherTool))
                .toolChoice("auto")
                .build();

        // Send request
        ChatCompletionResponse response = client.chat().createChatCompletion(request);

        if (response.isSuccess()) {
            // Handle function calling
            ChatMessage assistantMessage = response.getData().getChoices().get(0).getMessage();
            if (assistantMessage.getToolCalls() != null && !assistantMessage.getToolCalls().isEmpty()) {
                for (ToolCalls toolCall : assistantMessage.getToolCalls()) {
                    String functionName = toolCall.getFunction().getName();

                    if ("get_weather".equals(functionName)) {
                        Map<String, Object> result = getWeather("Beijing", null);
                        System.out.println("Weather info: " + result);
                    }
                }
            } else {
                System.out.println(assistantMessage.getContent());
            }
        } else {
            System.err.println("Error: " + response.getMsg());
        }
    }
}