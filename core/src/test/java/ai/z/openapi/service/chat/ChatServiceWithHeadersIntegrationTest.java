package ai.z.openapi.service.chat;

import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatRequestWithHeaders;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ChatRequestWithHeaders wrapper functionality. Tests the wrapper class
 * used for custom headers in chat requests.
 */
class ChatServiceWithHeadersIntegrationTest {

	@Test
	void testChatRequestWithHeaders_StreamingRequest() {
		// Arrange
		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model("glm-4-flash")
			.messages(Arrays
				.asList(ChatMessage.builder().role(ChatMessageRole.USER.value()).content("Hello, world!").build()))
			.stream(true)
			.build();

		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put("X-Custom-User-ID", "user123");
		customHeaders.put("X-Request-Source", "test");

		// Act
		ChatRequestWithHeaders wrapper = new ChatRequestWithHeaders(request, customHeaders);

		// Assert
		assertNotNull(wrapper);
		assertNotNull(wrapper.getRequest());
		assertNotNull(wrapper.getCustomHeaders());
		assertEquals(request, wrapper.getRequest());
		assertEquals(customHeaders, wrapper.getCustomHeaders());
		assertEquals(2, wrapper.getCustomHeaders().size());
		assertEquals("user123", wrapper.getCustomHeaders().get("X-Custom-User-ID"));
		assertEquals("test", wrapper.getCustomHeaders().get("X-Request-Source"));
	}

	@Test
	void testChatRequestWithHeaders_NonStreamingRequest() {
		// Arrange
		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model("glm-4-flash")
			.messages(Arrays
				.asList(ChatMessage.builder().role(ChatMessageRole.USER.value()).content("What is AI?").build()))
			.stream(false) // Non-streaming request
			.build();

		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put("X-Custom-User-ID", "user456");
		customHeaders.put("X-Request-Source", "test-sync");

		// Act
		ChatRequestWithHeaders wrapper = new ChatRequestWithHeaders(request, customHeaders);

		// Assert
		assertNotNull(wrapper);
		assertNotNull(wrapper.getRequest());
		assertNotNull(wrapper.getCustomHeaders());
		assertEquals(request, wrapper.getRequest());
		assertEquals(customHeaders, wrapper.getCustomHeaders());
		assertFalse(wrapper.getRequest().getStream()); // Verify non-streaming
		assertEquals("user456", wrapper.getCustomHeaders().get("X-Custom-User-ID"));
		assertEquals("test-sync", wrapper.getCustomHeaders().get("X-Request-Source"));
	}

	@Test
	void testChatRequestWithHeaders_NullHeaders() {
		// Arrange
		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model("glm-4-flash")
			.messages(Arrays
				.asList(ChatMessage.builder().role(ChatMessageRole.USER.value()).content("Test message").build()))
			.stream(false)
			.build();

		// Act
		ChatRequestWithHeaders wrapper = new ChatRequestWithHeaders(request, null);

		// Assert
		assertNotNull(wrapper);
		assertNotNull(wrapper.getRequest());
		assertEquals(request, wrapper.getRequest());
		// Custom headers are converted to empty map when null
		assertNull(wrapper.getCustomHeaders());
	}

	@Test
	void testChatRequestWithHeaders_ToString() {
		// Arrange
		ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
			.model("glm-4-flash")
			.messages(Arrays
				.asList(ChatMessage.builder().role(ChatMessageRole.USER.value()).content("Test message").build()))
			.stream(false)
			.build();

		Map<String, String> headers = new HashMap<>();
		headers.put("X-Test", "value");

		// Act
		ChatRequestWithHeaders wrapper = new ChatRequestWithHeaders(request, headers);
		String toStringResult = wrapper.toString();

		// Assert
		assertNotNull(toStringResult);
		assertTrue(toStringResult.contains("ChatRequestWithHeaders"));
	}

}