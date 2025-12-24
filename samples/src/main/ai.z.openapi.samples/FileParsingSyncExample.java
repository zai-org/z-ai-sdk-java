package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.fileparsing.FileParsingDownloadResponse;
import ai.z.openapi.service.fileparsing.FileParsingUploadReq;
import ai.z.openapi.utils.StringUtils;

public class FileParsingSyncExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient.builder().ofZHIPU().build()

        // Alternatively, the API Key can be specified directly in the code
        ZaiClient client = ZaiClient.builder()
                .apiKey("API Key")
                .build();

        try {
            System.out.println("=== Example: Creating file parsing task ===");

            String filePath = "your file path";
            FileParsingDownloadResponse result = syncFileParsingTaskExample(client, filePath, "pdf", "prime-sync");

            System.out.println("Parsing task created successfully, TaskId: " + result.getData().getTaskId());
            System.out.println("File content: " + result.getData().getContent());
            System.out.println("Download link: " + result.getData().getParsingResultUrl());

        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    /**
     * Example: Create parsing task (upload file and parse)
     *
     * @param client ZaiClient instance
     * @return Parsing task's taskId
     */
    private static FileParsingDownloadResponse syncFileParsingTaskExample(ZaiClient client, String filePath, String fileType, String toolType) {
        if (StringUtils.isEmpty(filePath)) {
            System.err.println("Invalid file path.");
            return null;
        }
        try {
            FileParsingUploadReq uploadReq = FileParsingUploadReq.builder()
                    .filePath(filePath)
                    .fileType(fileType)  // Supported types: pdf, docx, etc.
                    .toolType(toolType)  // Parsing tool type: lite, prime, expert
                    .build();

            System.out.println("Uploading file and creating parsing task...");
            return client.fileParsing().syncParse(uploadReq);
        } catch (Exception e) {
            System.err.println("File parsing task error: " + e.getMessage());
        }
        // Returning null means task creation failed
        return null;
    }


}