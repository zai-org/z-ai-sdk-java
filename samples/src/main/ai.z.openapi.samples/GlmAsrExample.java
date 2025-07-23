package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.audio.AudioTranscriptionRequest;
import ai.z.openapi.service.audio.AudioTranscriptionResponse;
import java.io.File;
import java.io.IOException;

public class GlmAsrExample {
    public static void main(String[] args) throws IOException {
        ZaiClient client = ZaiClient.builder().ofZHIPU()
                .apiKey("API-key")
                .build();
        File audioFile = new File("/resources/asr.wav");
        AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
                .model(Constants.ModelGLMASR)
                .file(audioFile)
                .stream(false)
                .build();

        AudioTranscriptionResponse response = client.audio().createTranscription(request);
        System.out.println(response.getData());
    }
}