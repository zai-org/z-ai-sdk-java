package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.audio.AudioSpeechRequest;
import ai.z.openapi.service.audio.AudioSpeechStreamingResponse;
import ai.z.openapi.service.model.Delta;
import ai.z.openapi.service.model.ModelData;

/**
 * Streaming Audio Speech Example
 * Demonstrates how to use ZaiClient for streaming text-to-speech conversion
 */
public class AudioSpeechStreamExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient.builder().ofZHIPU().build()
        ZaiClient client = ZaiClient.builder().ofZAI().build();

        // Create speech request with streaming enabled
        AudioSpeechRequest request = AudioSpeechRequest.builder()
                .model(Constants.ModelTTS)
                .input("你好，今天天气怎么样")
                .voice("tongtong")
                .responseFormat("pcm")
                .stream(true)  // Enable streaming response
                .build();

        try {
            // Execute streaming request
            AudioSpeechStreamingResponse response = client.audio().createStreamingSpeechStreaming(request);

            if (response.isSuccess() && response.getFlowable() != null) {
                System.out.println("Starting streaming TTS response...");

                response.getFlowable().subscribe(
                    data -> {
                        // Process each streaming response chunk
                        if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                            // Get content of current chunk
                            Delta delta = data.getChoices().get(0).getDelta();

                            // Print current audio content (base64 encoded)
                            if (delta != null && delta.getContent() != null) {
                                System.out.println("Received audio chunk: " + delta.getContent());
                            }
                        }
                    },
                    error -> System.err.println("\nStream error: " + error.getMessage()),
                    // Process streaming response completion event
                    () -> System.out.println("\nStreaming TTS completed")
                );

                // Wait for streaming to complete
                Thread.sleep(10000);
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
}