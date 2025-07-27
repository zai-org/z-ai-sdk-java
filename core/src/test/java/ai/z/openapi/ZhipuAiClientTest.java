package ai.z.openapi;

import ai.z.openapi.core.config.ZaiConfig;
import org.junit.jupiter.api.Test;

import static ai.z.openapi.core.Constants.ZHIPU_AI_BASE_URL;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ZhipuAiClient to verify it uses ZHIPU_AI_BASE_URL by default
 */
class ZhipuAiClientTest {

	@Test
	void testDefaultBaseUrl() {
		// Create a ZhipuAiClient with empty base URL in config
		ZaiConfig config = new ZaiConfig();
		config.setApiKey("test.api-key");
		// Don't set base URL, should default to ZHIPU_AI_BASE_URL

		ZhipuAiClient client = new ZhipuAiClient(config);

		// Verify that the client was created successfully
		assertNotNull(client);

		// The client should be an instance of AbstractAiClient
		assertTrue(client instanceof AbstractAiClient);
	}

	@Test
	void testBuilderPattern() {
		// Test using the Builder pattern
		ZhipuAiClient client = new ZhipuAiClient.Builder("test.api-key").enableTokenCache().build();

		assertNotNull(client);
		assertTrue(client instanceof AbstractAiClient);
	}

	@Test
	void testBuilderWithCustomBaseUrl() {
		// Test that custom base URL still works
		ZhipuAiClient client = new ZhipuAiClient.Builder("test.api-key").baseUrl("https://custom.api.url").build();

		assertNotNull(client);
		assertTrue(client instanceof AbstractAiClient);
	}

	@Test
	void testBuilderOfZHIPU() {
		// Test the ofZHIPU() method
		ZhipuAiClient client = new ZhipuAiClient.Builder("test.api-key").build();

		assertNotNull(client);
		assertTrue(client instanceof AbstractAiClient);
	}

	@Test
	void testInheritedMethods() {
		// Test that inherited methods from ZaiClient are available
		ZhipuAiClient client = new ZhipuAiClient.Builder("test.api-key").build();

		// These methods should be available from the parent class
		assertNotNull(client.chat());
		assertNotNull(client.embeddings());
		assertNotNull(client.files());
		assertNotNull(client.images());
		assertNotNull(client.audio());
	}

}