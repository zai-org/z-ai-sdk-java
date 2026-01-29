package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.layoutparsing.LayoutParsingCreateParams;
import ai.z.openapi.service.layoutparsing.LayoutParsingResponse;
import ai.z.openapi.service.layoutparsing.LayoutParsingResult;

public class LayoutParsingExample {

	public static void main(String[] args) {
		// Create client, recommended to set API Key via environment variable
		// export ZAI_API_KEY=your.api_key
		// for Z.ai use the `ZaiClient`, for Zhipu AI use the ZhipuAiClient.builder().ofZHIPU().build()

		// You can also set the API Key directly in the code for testing
		ZaiClient client = ZaiClient.builder().ofZAI().build();

		try {
			System.out.println("=== Layout Parsing Example ===");

			// Example 1: Using URL
			String imageUrl = "https://cdn.bigmodel.cn/static/platform/images/trialcenter/example/visual_img1.jpeg";
			LayoutParsingResponse response = layoutParsingExample(client, "glm-ocr", imageUrl, true);
			printResult(response);

		} catch (Exception e) {
			System.err.println("Exception occurred: " + e.getMessage());
			e.printStackTrace();
		} finally {
			client.close();
		}
	}

	/**
	 * Example: Perform layout parsing on an image or PDF
	 * @param client ZaiClient instance
	 * @param model Model name, e.g., "glm-ocr"
	 * @param file Image URL or base64 string
	 * @param useLayoutDetails Whether to return detailed layout information (default true)
	 * @param userId Optional user ID for tracking
	 * @param requestId Optional request ID for tracking
	 * @return LayoutParsingResponse object
	 */
	private static LayoutParsingResponse layoutParsingExample(ZaiClient client, String model, String file,
															 Boolean useLayoutDetails) {
		if (model == null || model.trim().isEmpty()) {
			System.err.println("Model cannot be null or empty.");
			return null;
		}
		if (file == null || file.trim().isEmpty()) {
			System.err.println("File (URL or base64) cannot be null or empty.");
			return null;
		}
		try {
			LayoutParsingCreateParams params = LayoutParsingCreateParams.builder()
					.model(model)
					.file(file)
					.useLayoutDetails(useLayoutDetails)
					.build();

			System.out.println("Request parameters:");
			System.out.println("  model: " + model);
			System.out.println("  file: " + (file.length() > 80 ? file.substring(0, 80) + "..." : file));
			System.out.println("  useLayoutDetails: " + (useLayoutDetails != null ? useLayoutDetails : "default true"));
			System.out.println();

			System.out.println("Calling layout parsing API...");
			return client.layoutParsing().layoutParsing(params);
		}
		catch (Exception e) {
			System.err.println("Layout parsing task error: " + e.getMessage());
		}
		// Return null indicates failure
		return null;
	}

	private static void printResult(LayoutParsingResponse response) {
		if (response == null) {
			System.out.println("No response received.");
			return;
		}
		System.out.println("Response status: " + (response.isSuccess() ? "SUCCESS" : "FAILED"));
		System.out.println("Code: " + response.getCode());
		System.out.println("Message: " + response.getMsg());
		if (!response.isSuccess() && response.getError() != null) {
			System.out.println("Error: " + response.getError());
		}
		if (response.getData() != null) {
			LayoutParsingResult data = response.getData();
			System.out.println("Task ID: " + data.getId());
			System.out.println("Created: " + data.getCreated());
			System.out.println("Model: " + data.getModel());
			System.out.println("Request ID: " + data.getRequestId());
			System.out.println("Markdown results length: " + (data.getMdResults() != null ? data.getMdResults().length() : 0));
			if (data.getLayoutDetails() != null) {
				System.out.println("Layout pages count: " + data.getLayoutDetails().size());
				data.getLayoutDetails().stream().limit(1).forEach(page -> {
					System.out.println("First page blocks count: " + (page != null ? page.size() : 0));
					if (page != null) {
						page.stream().limit(3).forEach(detail -> {
							System.out.println(
									"  - index: " + detail.getIndex() + ", label: " + detail.getLabel() + ", bbox: " + detail.getBbox2d());
						});
					}
				});
			}
			if (data.getDataInfo() != null) {
				System.out.println("Data info:");
				System.out.println("  num_pages: " + data.getDataInfo().getNumPages());
				if (data.getDataInfo().getPages() != null && !data.getDataInfo().getPages().isEmpty()) {
					System.out.println("  first page size: " + data.getDataInfo().getPages().get(0).getWidth() + "x" + data.getDataInfo().getPages().get(0).getHeight());
				}
			}
			if (data.getUsage() != null) {
				System.out.println("Usage:");
				System.out.println("  prompt_tokens: " + data.getUsage().getPromptTokens());
				System.out.println("  completion_tokens: " + data.getUsage().getCompletionTokens());
				System.out.println("  total_tokens: " + data.getUsage().getTotalTokens());
			}
		}
	}
}
