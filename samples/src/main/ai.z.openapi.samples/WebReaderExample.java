package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.web_reader.WebReaderRequest;
import ai.z.openapi.service.web_reader.WebReaderResponse;
import ai.z.openapi.service.web_reader.WebReaderResult;

/**
 * Web Reader Example
 * Demonstrates how to use ZaiClient for web page reading and parsing capabilities
 */
public class WebReaderExample {

    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api_key
        // for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient.builder().ofZHIPU().build()
        ZaiClient client = ZaiClient.builder().ofZAI().build();

        basicWebReader(client);
    }

    /**
     * Example of basic web reader functionality
     */
    private static void basicWebReader(ZaiClient client) {
        System.out.println("\n=== Web Reader Example ===");

        // Create web reader request
        WebReaderRequest request = WebReaderRequest.builder()
            .url("https://example.com/")
            .returnFormat("markdown")
            .withImagesSummary(Boolean.TRUE)
            .withLinksSummary(Boolean.TRUE)
            .build();

        try {
            // Execute request
            WebReaderResponse response = client.webReader().createWebReader(request);

            if (response.isSuccess()) {
                System.out.println("Read successful!");

                WebReaderResult result = response.getData();
                if (result != null && result.getReaderResult() != null) {
                    WebReaderResult.ReaderData data = result.getReaderResult();
                    System.out.println("Title: " + data.getTitle());
                    System.out.println("URL: " + data.getUrl());
                    System.out.println("Description: " + data.getDescription());

                    String content = data.getContent();
                    if (content != null) {
                        String preview = content.length() > 300 ? content.substring(0, 300) + "..." : content;
                        System.out.println("\nContent preview:\n" + preview);
                    }

                    if (data.getImages() != null) {
                        System.out.println("\nImages count: " + data.getImages().size());
                    }
                    if (data.getLinks() != null) {
                        System.out.println("Links count: " + data.getLinks().size());
                    }
                }
                else {
                    System.out.println("No reader result returned.");
                }
            }
            else {
                System.err.println("Error: " + response.getMsg());
                if (response.getError() != null) {
                    System.err.println("Error detail: " + response.getError());
                }
            }
        }
        catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
}