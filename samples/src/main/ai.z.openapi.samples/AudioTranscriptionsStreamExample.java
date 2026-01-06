package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.audio.AudioTranscriptionChunk;
import ai.z.openapi.service.audio.AudioTranscriptionRequest;
import ai.z.openapi.service.audio.AudioTranscriptionResponse;

import java.io.File;

/**
 * Streaming Audio Transcription Example
 * Demonstrates how to use ZaiClient for streaming audio transcription (speech-to-text)
 */
public class AudioTranscriptionsStreamExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient.builder().ofZHIPU().build()
        ZaiClient client = ZaiClient.builder().ofZAI().build();

        // Prepare audio file
        // Supported formats: .wav, .mp3
        // Limitations: file size ≤ 25 MB, duration ≤ 30 seconds
        // The sample audio file is located at: samples/src/main/resources/asr.wav
        File audioFile = new File("samples/src/main/resources/asr.wav");

        // Create transcription request with streaming enabled
        AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
                .model(Constants.ModelGLMASR)
                .file(audioFile)
                .stream(true)  // Enable streaming response
                .build();

        try {
            // Execute streaming request
            AudioTranscriptionResponse response = client.audio().createTranscription(request);

            if (response.isSuccess() && response.getFlowable() != null) {
                System.out.println("Starting streaming transcription...");

                response.getFlowable().subscribe(
                    chunk -> {
                        // Process each streaming response chunk
                        // delta is already a String containing the text content
                        if (chunk.getDelta() != null) {
                            String delta = chunk.getDelta();
                            // Print each chunk on a new line to show streaming effect
                            System.out.println(delta);
                        }
                    },
                    error -> System.err.println("Stream error: " + error.getMessage()),
                    () -> System.out.println("Streaming transcription completed")
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