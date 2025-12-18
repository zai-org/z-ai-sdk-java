package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.fileparsing.FileParsingDownloadReq;
import ai.z.openapi.service.fileparsing.FileParsingDownloadResponse;
import ai.z.openapi.service.fileparsing.FileParsingResponse;
import ai.z.openapi.service.fileparsing.FileParsingUploadReq;
import ai.z.openapi.utils.StringUtils;

public class FileParsingExample {

    public static void main(String[] args) {
// It's recommended to set the API Key via environment variable
// export ZAI_API_KEY=your.api_key
// ZaiClient client = ZaiClient.builder().build();

// You can also specify the API Key directly in code


        ZaiClient client = ZaiClient.builder()
                .apiKey("API Key")
                .build();

        try {
            // Example 1: Create a file parsing task
            System.out.println("=== Example: Create file parsing task ===");
            String filePath = "your file path";
            String taskId = createFileParsingTaskExample(client, filePath, "pdf", "lite");

            // Example 2: Get parsing result
            System.out.println("\n=== Example: Get parsing result ===");
            getFileParsingResultExample(client, taskId);

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
     * @return TaskId of the parsing task
     */
    private static String createFileParsingTaskExample(ZaiClient client, String filePath, String fileType, String toolType) {
        if (StringUtils.isEmpty(filePath)) {
            System.err.println("Invalid file path.");
            return null;
        }
        try {
            FileParsingUploadReq uploadReq = FileParsingUploadReq.builder()
                    .filePath(filePath)
                    .fileType(fileType)  // support: pdf, docx etc.
                    .toolType(toolType) // tool type: lite, prime, expert
                    .build();

            System.out.println("Uploading file and creating parsing task...");
            FileParsingResponse response = client.fileParsing().createParseTask(uploadReq);
            if (response.isSuccess()) {
                if (null != response.getData().getTaskId()) {
                    String taskId = response.getData().getTaskId();
                    System.out.println("Parsing task created successfully, TaskId: " + taskId);
                    return taskId;
                } else {
                    System.err.println("Failed to create parsing task: " + response.getData().getMessage());
                }
            } else {
                System.err.println("Failed to create parsing task: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("File parsing task error: " + e.getMessage());
        }
        // Return null indicates task creation failed
        return null;
    }

    /**
     * Example: Get parsing result
     *
     * @param client ZaiClient instance
     * @param taskId ID of the parsing task
     */
    private static void getFileParsingResultExample(ZaiClient client, String taskId) {
        if (taskId == null || taskId.isEmpty()) {
            System.err.println("Invalid task ID. Cannot get parsing result.");
            return;
        }

        try {
            int maxRetry = 100;      // Maximum 100 polling attempts
            int intervalMs = 3000;   // 3 seconds interval between each polling
            for (int i = 0; i < maxRetry; i++) {
                FileParsingDownloadReq downloadReq = FileParsingDownloadReq.builder()
                        .taskId(taskId)
                        .formatType("text")
                        .build();

                FileParsingDownloadResponse response = client.fileParsing().getParseResult(downloadReq);

                if (response.isSuccess()) {
                    String status = response.getData().getStatus();
                    System.out.println("Current task status: " + status);

                    if ("succeeded".equalsIgnoreCase(status)) {
                        System.out.println("Parsing result obtained successfully!");
                        System.out.println("Parsed content: " + response.getData().getContent());
                        System.out.println("Download link: " + response.getData().getParsingResultUrl());
                        return;
                    } else if ("processing".equalsIgnoreCase(status)) {
                        System.out.println("Parsing in progress, please wait...");
                        Thread.sleep(intervalMs);
                    } else {
                        System.out.println("Parsing task exception, status: " + status + ", message: " + response.getData().getMessage());
                        return;
                    }
                } else {
                    System.err.println("Failed to get parsing result: " + response.getMsg());
                    return;
                }
            }
            System.out.println("Wait timeout, please check the parsing result later.");
        } catch (Exception e) {
            System.err.println("Exception occurred while getting parsing result: " + e.getMessage());
        }
    }
}
