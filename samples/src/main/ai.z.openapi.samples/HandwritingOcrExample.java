package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.ocr.HandwritingOcrResponse;
import ai.z.openapi.service.ocr.HandwritingOcrResult;
import ai.z.openapi.service.ocr.HandwritingOcrUploadReq;
import ai.z.openapi.service.ocr.WordsResult;

public class HandwritingOcrExample {

	public static void main(String[] args) {
		// It is recommended to set the API Key via environment variable
		// export ZAI_API_KEY=your.api_key
		// ZaiClient client = ZaiClient.builder().build();

		// You can also set the API Key directly in the code for testing
		ZaiClient client = ZaiClient.builder()
				 .apiKey("your-real-api-key")
				.build();

		try {
			System.out.println("=== Handwriting OCR Example ===");

			String filePath = ""; // Change to your own image path
			HandwritingOcrResponse response = syncHandwritingOcrExample(client, filePath, "hand_write", "CHN_ENG", true);
			if (response != null && response.getData() != null) {
				System.out.println(response.getData());
			} else {
				System.out.println("Recognition failed.");
			}
		} catch (Exception e) {
			System.err.println("Exception occurred: " + e.getMessage());
			e.printStackTrace();
		} finally {
            client.close();
        }
	}

	/**
	 * Example: Upload an image and perform handwriting OCR recognition
	 * @param client ZaiClient instance
	 * @param filePath Path of the image file
	 * @param toolType Type of recognition tool
	 * @param languageType Language type (optional)
	 * @return OCR response object
	 */
	private static HandwritingOcrResponse syncHandwritingOcrExample(ZaiClient client, String filePath, String toolType,
																	String languageType, Boolean probability) {
		if (filePath == null || filePath.trim().isEmpty()) {
			System.err.println("Invalid file path.");
			return null;
		}
		try {
			HandwritingOcrUploadReq uploadReq = new HandwritingOcrUploadReq();
			uploadReq.setFilePath(filePath);
			uploadReq.setToolType(toolType); // Must be "hand_write"
			uploadReq.setLanguageType(languageType); // Can be "CHN_ENG", "ENG", etc.
			uploadReq.setProbability(probability);
			System.out.println(uploadReq.toString());
			System.out.println("Uploading the image and performing handwriting recognition...calling API");
			return client.handwriting().recognize(uploadReq);
		}
		catch (Exception e) {
			System.err.println("Handwriting recognition task error: " + e.getMessage());
		}
		// Return null indicates failure
		return null;
	}
}