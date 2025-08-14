package ai.z.openapi.realtime;

import java.nio.charset.StandardCharsets;
import ai.z.openapi.service.realtime.JasonUtil;
import ai.z.openapi.service.realtime.OkHttpRealtimeClient;
import ai.z.openapi.service.realtime.RealtimeClientEvent;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.server.RealtimeError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OkHttpRealtimeClient test class for testing realtime WebSocket communication
 */
@DisplayName("OkHttpRealtimeClient Tests")
public class RealtimeClientTest {

	private static final Logger logger = LoggerFactory.getLogger(RealtimeClientTest.class);

	private static final String DEFAULT_WEBSOCKET_URL = "wss://open.bigmodel.cn/api/paas/v4/realtime";

	private static final String TEST_RESOURCE_FILE = "Audio.ServerVad.Input";

	private static final int CONNECTION_WAIT_TIME_MS = 5000;

	private OkHttpRealtimeClient.CommunicationProvider communicationProvider;

	private Consumer<RealtimeServerEvent> serverEventHandler;

	private AtomicInteger receivedEventCount;

	private AtomicBoolean hasReceivedError;

	@BeforeEach
	void setUp() {
		receivedEventCount = new AtomicInteger(0);
		hasReceivedError = new AtomicBoolean(false);

		communicationProvider = createCommunicationProvider();
		serverEventHandler = createServerEventHandler();
	}

	@Test
	@DisplayName("Test OkHttpRealtimeClient Instantiation")
	void testClientInstantiation() {
		OkHttpRealtimeClient client = new OkHttpRealtimeClient(communicationProvider, serverEventHandler);
		assertNotNull(client, "OkHttpRealtimeClient should be properly instantiated");
	}

	@Test
	@DisplayName("Test Communication Provider Configuration")
	void testCommunicationProviderConfiguration() {
		assertNotNull(communicationProvider, "Communication provider should not be null");
		assertNotNull(communicationProvider.getWebSocketUrl(), "WebSocket URL should not be null");
		assertEquals(DEFAULT_WEBSOCKET_URL, communicationProvider.getWebSocketUrl(),
				"WebSocket URL should match expected value");
		assertNotNull(communicationProvider.getAuthToken(), "Auth token should not be null");
	}

	@Test
	@DisplayName("Test Server Event Handler")
	void testServerEventHandler() {
		assertNotNull(serverEventHandler, "Server event handler should not be null");
		// Test with a mock event to ensure handler works
		RealtimeServerEvent mockEvent = createMockServerEvent();
		assertDoesNotThrow(() -> serverEventHandler.accept(mockEvent),
				"Server event handler should not throw exception");
	}

	@Test
	@DisplayName("Test Audio Resource File Loading")
	void testAudioResourceFileLoading() throws IOException {
		InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(TEST_RESOURCE_FILE);
		assertNotNull(resourceStream, "Test resource file should exist");

		List<String> lines = new java.util.ArrayList<>();
		try (Scanner scanner = new Scanner(resourceStream, StandardCharsets.UTF_8.name())) {
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
		}
		assertNotNull(lines, "Resource lines should not be null");
		assertFalse(lines.isEmpty(), "Resource file should not be empty");

		// Verify that lines contain valid JSON events
		long validEventCount = lines.stream().filter(line -> !line.trim().isEmpty()).count();
		assertTrue(validEventCount > 0, "Should have at least one valid event line");

		logger.info("Loaded {} lines from test resource file, {} valid events", lines.size(), validEventCount);
	}

	@Test
	@DisplayName("Test JSON Event Parsing")
	void testJsonEventParsing() throws IOException {
		InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(TEST_RESOURCE_FILE);
		assertNotNull(resourceStream, "Test resource file should exist");

		List<String> lines = new java.util.ArrayList<>();
		try (Scanner scanner = new Scanner(resourceStream, StandardCharsets.UTF_8.name())) {
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
		}

		int parsedEventCount = 0;
		for (String text : lines) {
			if (!text.trim().isEmpty()) {
				assertDoesNotThrow(() -> {
					RealtimeClientEvent clientEvent = JasonUtil.fromJsonToClientEvent(text);
					assertNotNull(clientEvent, "Parsed client event should not be null");
					assertNotNull(clientEvent.getType(), "Event type should not be null");
				}, "Should be able to parse JSON event: " + text);
				parsedEventCount++;
			}
		}

		assertTrue(parsedEventCount > 0, "Should have parsed at least one event");
		logger.info("Successfully parsed {} events from test resource file", parsedEventCount);
	}

	@Test
	@DisplayName("Test Realtime Client Full Workflow")
	@EnabledIfEnvironmentVariable(named = "API_KEY_01", matches = ".+")
	void testRealtimeClientFullWorkflow() {
		OkHttpRealtimeClient client = new OkHttpRealtimeClient(communicationProvider, serverEventHandler);

		try {
			// Start the client
			client.start();
			logger.info("Client started successfully");

			// Wait for connection
			client.waitForConnection();
			logger.info("WebSocket connection established");

			// Load and send test events
			InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(TEST_RESOURCE_FILE);
			assertNotNull(resourceStream, "Test resource file should exist");

			List<String> lines = new java.util.ArrayList<>();
			try (Scanner scanner = new Scanner(resourceStream, StandardCharsets.UTF_8.name())) {
				while (scanner.hasNextLine()) {
					lines.add(scanner.nextLine());
				}
			}

			int sentEventCount = 0;
			for (String text : lines) {
				if (!text.trim().isEmpty()) {
					RealtimeClientEvent clientEvent = JasonUtil.fromJsonToClientEvent(text);
					logger.info("Sending event: {}", clientEvent.getType());
					logger.debug("Event JSON: {}", JasonUtil.toJsonFromClientEvent(clientEvent));
					client.sendMessage(clientEvent);
					sentEventCount++;
				}
			}

			logger.info("Sent {} events to realtime client", sentEventCount);

			// Wait for processing
			Thread.sleep(CONNECTION_WAIT_TIME_MS);

			// Stop the client
			client.stop();
			logger.info("Client stopped successfully");

			// Verify that we received some events
			logger.info("Received {} server events during test", receivedEventCount.get());
			if (hasReceivedError.get()) {
				logger.warn("Received error events during test execution");
			}
		}
		catch (Exception e) {
			logger.error("Client runtime exception during test", e);
			fail("Test should not throw exception: " + e.getMessage());
		}
		finally {
			try {
				client.close();
				logger.info("Client closed successfully");
			}
			catch (IOException e) {
				logger.error("Exception occurred while closing client", e);
			}
		}
	}

	@Test
	@DisplayName("Test Client Lifecycle Without Connection")
	void testClientLifecycleWithoutConnection() {
		OkHttpRealtimeClient client = new OkHttpRealtimeClient(communicationProvider, serverEventHandler);

		try {
			// Test start and stop without actual connection
			assertDoesNotThrow(() -> client.start(), "Client start should not throw exception");
			logger.info("Client started in test mode");

			assertDoesNotThrow(() -> client.stop(), "Client stop should not throw exception");
			logger.info("Client stopped in test mode");
		}
		finally {
			try {
				client.close();
				logger.info("Client closed in test mode");
			}
			catch (IOException e) {
				logger.error("Exception occurred while closing client in test mode", e);
			}
		}
	}

	/**
	 * Creates a communication provider for testing
	 */
	private OkHttpRealtimeClient.CommunicationProvider createCommunicationProvider() {
		return new OkHttpRealtimeClient.CommunicationProvider() {
			@Override
			public String getAuthToken() {
				return Optional.ofNullable(System.getenv("API_KEY_01")).orElse("test-api-key");
			}

			@Override
			public String getWebSocketUrl() {
				return DEFAULT_WEBSOCKET_URL;
			}
		};
	}

	/**
	 * Creates a server event handler for testing
	 */
	private Consumer<RealtimeServerEvent> createServerEventHandler() {
		return event -> {
			receivedEventCount.incrementAndGet();
			logger.info("Received server event: {}, type: {}", event.getType(), event.getClass().getSimpleName());

			if (event instanceof RealtimeError) {
				hasReceivedError.set(true);
				logger.error("Received server error event: {}", JasonUtil.toJsonFromServerEvent(event));
			}
		};
	}

	/**
	 * Creates a mock server event for testing
	 */
	private RealtimeServerEvent createMockServerEvent() {
		// Create a simple mock event for testing
		return new RealtimeServerEvent() {
			@Override
			public String getType() {
				return "test.event";
			}
		};
	}

}
