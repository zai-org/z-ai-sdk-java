package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.audio.AudioTranscriptionRequest;
import ai.z.openapi.service.audio.AudioTranscriptionResponse;

import java.io.File;

/**
 * Audio Transcriptions Example
 * Demonstrates how to use ZaiClient for audio transcription (speech-to-text)
 */
public class AudioTranscriptionsExample {

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

        // Create transcription request
        AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
                .model(Constants.ModelGLMASR)
                .file(audioFile)
                .stream(false)
                .build();

        try {
            // Execute request
            AudioTranscriptionResponse response = client.audio().createTranscription(request);

            if (response.isSuccess()) {
                System.out.println("Transcription Result:");
                String text = response.getData().getText();
                // Remove leading newline if present
                if (text != null && text.startsWith("\n")) {
                    text = text.substring(1);
                }
                System.out.println(text);
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