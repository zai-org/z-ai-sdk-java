package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.file.FileApiResponse;
import ai.z.openapi.service.file.FileService;
import ai.z.openapi.service.file.FileUploadParams;
import ai.z.openapi.service.file.UploadFilePurpose;
import ai.z.openapi.service.voiceclone.*;

import java.nio.file.Paths;

/**
 * Voice Clone Example
 * Demonstrates how to use ZaiClient for voice cloning operations including:
 * - Uploading voice samples
 * - Creating voice clones
 * - Listing existing voices
 * - Deleting voice clones
 */
public class VoiceCloneExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        ZaiClient client = ZaiClient.builder().build();

        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder()
        //         .apiKey("your.api_key")
        //         .build();

        VoiceCloneService voiceCloneService = client.voiceClone();
        FileService fileService = client.files();

        try {
            // Example 1: Create a voice clone
            System.out.println("=== Voice Clone Creation Example ===");
            createVoiceCloneExample(voiceCloneService, fileService);

            // Example 2: List existing voices
            System.out.println("\n=== Voice List Example ===");
            listVoicesExample(voiceCloneService);

            // Example 3: Delete a voice clone
            System.out.println("\n=== Voice Deletion Example ===");
            deleteVoiceExample(voiceCloneService);

        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Example of creating a voice clone with file upload
     */
    private static void createVoiceCloneExample(VoiceCloneService voiceCloneService, FileService fileService) {
        try {
            // Step 1: Upload the voice input audio file
            String voiceInputFilePath = Paths.get("samples", "resources", "voice_clone_input.mp3").toString();
            
            FileUploadParams uploadRequest = FileUploadParams.builder()
                    .filePath(voiceInputFilePath)
                    .purpose(UploadFilePurpose.VOICE_CLONE_INPUT.value())
                    .requestId("voice-clone-example-" + System.currentTimeMillis())
                    .build();

            System.out.println("Uploading voice input file...");
            FileApiResponse uploadResponse = fileService.uploadFile(uploadRequest);

            if (uploadResponse.isSuccess()) {
                String fileId = uploadResponse.getData().getId();
                System.out.println("Voice input file uploaded successfully with ID: " + fileId);

                // Step 2: Create voice clone using the uploaded file
                VoiceCloneRequest request = new VoiceCloneRequest();
                request.setVoiceName("My Custom Voice");
                request.setText("Hello, this is a sample text for voice cloning training");
                request.setInput("Welcome to our voice synthesis system");
                request.setFileId(fileId);
                request.setRequestId("clone-request-" + System.currentTimeMillis());
                request.setModel("CogTTS-clone");

                System.out.println("Creating voice clone...");
                VoiceCloneResponse response = voiceCloneService.cloneVoice(request);

                if (response.isSuccess()) {
                    System.out.println("Voice clone created successfully!");
                    System.out.println("Voice: " + response.getData().getVoice());
                } else {
                    System.err.println("Voice clone creation failed: " + response.getMsg());
                    if (response.getError() != null) {
                        System.err.println("Error details: " + response.getError().getMessage());
                    }
                }
            } else {
                System.err.println("File upload failed: " + uploadResponse.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Error in voice clone creation: " + e.getMessage());
        }
    }

    /**
     * Example of listing existing voice clones
     */
    private static void listVoicesExample(VoiceCloneService voiceCloneService) {
        try {
            // List all private voices
            VoiceListRequest request = new VoiceListRequest();
            request.setVoiceType("PRIVATE"); // Filter for custom voice clones
            request.setRequestId("list-request-" + System.currentTimeMillis());

            System.out.println("Retrieving voice list...");
            VoiceListResponse response = voiceCloneService.listVoice(request);

            if (response.isSuccess()) {
                System.out.println("Voice list retrieved successfully!");
                if (response.getData().getVoiceList() != null && !response.getData().getVoiceList().isEmpty()) {
                    System.out.println("Found " + response.getData().getVoiceList().size() + " voices:");
                    for (VoiceData voice : response.getData().getVoiceList()) {
                        System.out.println("- Voice: " + voice.getVoice());
                        System.out.println("  Name: " + voice.getVoiceName());
                        System.out.println("  Type: " + voice.getVoiceType());
                        if (voice.getDownloadUrl() != null) {
                            System.out.println("  Download URL: " + voice.getDownloadUrl());
                        }
                        if (voice.getCreateTime() != null) {
                            System.out.println("  Created: " + voice.getCreateTime());
                        }
                        System.out.println();
                    }
                } else {
                    System.out.println("No voices found.");
                }
            } else {
                System.err.println("Voice list retrieval failed: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Error in voice list retrieval: " + e.getMessage());
        }
    }

    /**
     * Example of deleting a voice clone
     * Note: Replace "your-voice" with an actual voice from your account
     */
    private static void deleteVoiceExample(VoiceCloneService voiceCloneService) {
        try {
            // Note: This is just an example - replace with actual voice
            String voiceToDelete = "your-voice-here";
            
            VoiceDeleteRequest request = new VoiceDeleteRequest();
            request.setVoice(voiceToDelete);
            request.setRequestId("delete-request-" + System.currentTimeMillis());

            System.out.println("Deleting voice: " + voiceToDelete);
            VoiceDeleteResponse response = voiceCloneService.deleteVoice(request);

            if (response.isSuccess()) {
                System.out.println("Voice clone deleted successfully!");
                if (response.getData().getUpdateTime() != null) {
                    System.out.println("Deletion time: " + response.getData().getUpdateTime());
                }
            } else {
                System.err.println("Voice deletion failed: " + response.getMsg());
                if (response.getError() != null) {
                    System.err.println("Error details: " + response.getError().getMessage());
                }
            }
        } catch (Exception e) {
            // Expected to fail with example voice
            System.out.println("Note: This example uses a placeholder voice and is expected to fail.");
            System.out.println("Replace 'your-voice-here' with an actual voice to test deletion.");
        }
    }
}