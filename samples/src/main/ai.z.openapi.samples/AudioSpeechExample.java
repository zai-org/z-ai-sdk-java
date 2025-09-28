package ai.z.openapi.samples;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.audio.AudioSpeechRequest;
import ai.z.openapi.service.audio.AudioSpeechResponse;

/**
 * Audio Speech Example
 * Demonstrates how to use ZaiClient for audio speech
 */
public class AudioSpeechExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient
        ZhipuAiClient client = ZhipuAiClient.builder().build();

        // Create request
        AudioSpeechRequest request = AudioSpeechRequest.builder()
            .model(Constants.ModelTTS)
            .input("Hello, this is a test for text-to-speech functionality.")
            .voice("tongtong")
            .build();

        try {
            // Execute request
            AudioSpeechResponse response = client.audio().createSpeech(request);

            if (response.isSuccess()) {
                System.out.println("Response: " + response.getData());
            } else {
                System.err.println("Error: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}