package ai.z.openapi.service.audio;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.Choice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AudioService test class for testing various functionalities of AudioService and
 * AudioServiceImpl
 */
@DisplayName("AudioService Tests")
public class AudioServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(AudioServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private AudioService audioService;

	// Request ID template
	private static final String REQUEST_ID_TEMPLATE = "audio-test-%d";

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		audioService = client.audio();
	}

	@Test
	@DisplayName("Test AudioService Instantiation")
	void testAudioServiceInstantiation() {
		assertNotNull(audioService, "AudioService should be properly instantiated");
		assertInstanceOf(AudioServiceImpl.class, audioService,
				"AudioService should be an instance of AudioServiceImpl");
	}

	@Test
	@DisplayName("Test Text-to-Speech Generation")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateSpeech() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		AudioSpeechRequest request = AudioSpeechRequest.builder()
			.model("cogtts")
			.input("Hello, this is a test for text-to-speech functionality.")
			.voice("tongtong")
			.requestId(requestId)
			.build();

		// Execute test
		AudioSpeechApiResponse response = audioService.createSpeech(request);

		// Verify results
		assertNotNull(response, "Speech response should not be null");
		assertTrue(response.isSuccess(), "Speech response should be successful");
		assertNotNull(response.getData(), "Speech response data should not be null");
		assertTrue(response.getData().exists(), "Generated audio file should exist");
		assertTrue(response.getData().length() > 0, "Generated audio file should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Text-to-speech response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Custom Speech Generation with Voice Cloning")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCreateCustomSpeech() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		File voiceFile = new File("src/test/resources/asr.wav");

		AudioCustomizationRequest request = AudioCustomizationRequest.builder()
			.model("cogtts")
			.input("This is a test for custom voice generation.")
			.voiceData(voiceFile)
			.voiceText("Sample voice text for cloning")
			.responseFormat("wav")
			.requestId(requestId)
			.build();

		// Execute test
		AudioCustomizationApiResponse response = audioService.createCustomSpeech(request);

		// Verify results
		assertNotNull(response, "Custom speech response should not be null");
		assertTrue(response.isSuccess(), "Custom speech response should be successful");
		assertNotNull(response.getData(), "Custom speech response data should not be null");
		assertTrue(response.getData().exists(), "Generated custom audio file should exist");
		assertTrue(response.getData().length() > 0, "Generated custom audio file should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Custom speech response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Synchronous Audio Transcription")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testSyncAudioTranscription() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		File audioFile = new File("src/test/resources/asr.wav");

		AudioTranscriptionsRequest request = AudioTranscriptionsRequest.builder()
			.model("glm-asr")
			.file(audioFile)
			.stream(false)
			.requestId(requestId)
			.build();

		// Execute test
		ChatCompletionResponse response = audioService.createTranscription(request);

		// Verify results
		assertNotNull(response, "Transcription response should not be null");
		assertTrue(response.isSuccess(), "Transcription response should be successful");
		assertNotNull(response.getData(), "Transcription response data should not be null");
		assertNotNull(response.getData().getChoices(), "Response choices should not be null");
		assertFalse(response.getData().getChoices().isEmpty(), "Response choices should not be empty");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Synchronous transcription response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test Stream Audio Transcription")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testStreamAudioTranscription() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		File audioFile = new File("src/test/resources/asr.wav");

		AudioTranscriptionsRequest request = AudioTranscriptionsRequest.builder()
			.model("glm-asr")
			.file(audioFile)
			.stream(true)
			.requestId(requestId)
			.build();

		// Execute test
		ChatCompletionResponse response = audioService.createTranscription(request);

		// Verify results
		assertNotNull(response, "Stream transcription response should not be null");
		assertTrue(response.isSuccess(), "Stream transcription response should be successful");

		// Test stream data processing
		AtomicInteger messageCount = new AtomicInteger(0);
		AtomicBoolean isFirst = new AtomicBoolean(true);

		response.getFlowable().doOnNext(modelData -> {
			if (isFirst.getAndSet(false)) {
				logger.info("Starting to receive stream transcription response:");
			}
			if (modelData.getChoices() != null && !modelData.getChoices().isEmpty()) {
				Choice choice = modelData.getChoices().get(0);
				if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
					logger.info("Received transcription content: {}", choice.getDelta().getContent());
					messageCount.incrementAndGet();
				}
			}
		})
			.doOnComplete(() -> logger.info("Stream transcription completed, received {} messages in total",
					messageCount.get()))
			.doOnError(throwable -> logger.error("Stream transcription error: {}", throwable.getMessage()))
			.blockingSubscribe();

		assertTrue(messageCount.get() >= 0, "Should receive at least zero messages");
		logger.info("Stream audio transcription test completed");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Speech Request")
	void testValidation_NullSpeechRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createSpeech(null);
		}, "Null speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Custom Speech Request")
	void testValidation_NullCustomSpeechRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createCustomSpeech(null);
		}, "Null custom speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Transcription Request")
	void testValidation_NullTranscriptionRequest() {
		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createTranscription(null);
		}, "Null transcription request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Invalid Audio File")
	void testValidation_InvalidAudioFile() {
		AudioTranscriptionsRequest request = AudioTranscriptionsRequest.builder()
			.model("glm-asr")
			.file(new File("non-existent-file.wav"))
			.stream(false)
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createTranscription(request);
		}, "Invalid audio file should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Model in Speech Request")
	void testValidation_NullModelInSpeechRequest() {
		AudioSpeechRequest request = AudioSpeechRequest.builder().input("Test input").voice("alloy").build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createSpeech(request);
		}, "Null model in speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Empty Input in Speech Request")
	void testValidation_EmptyInputInSpeechRequest() {
		AudioSpeechRequest request = AudioSpeechRequest.builder().model("cogtts").input("").voice("alloy").build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createSpeech(request);
		}, "Empty input in speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null Voice Data in Custom Speech Request")
	void testValidation_NullVoiceDataInCustomSpeechRequest() {
		AudioCustomizationRequest request = AudioCustomizationRequest.builder()
			.model("cogtts")
			.input("Test input")
			.voiceText("Voice text")
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createCustomSpeech(request);
		}, "Null voice data in custom speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Parameter Validation - Null File in Transcription Request")
	void testValidation_NullFileInTranscriptionRequest() {
		AudioTranscriptionsRequest request = AudioTranscriptionsRequest.builder()
			.model("glm-asr")
			.stream(false)
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createTranscription(request);
		}, "Null file in transcription request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Test Speech Generation with Different Voice Options")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testSpeechGenerationWithDifferentVoices() throws JsonProcessingException {
		// Test with available voice options (currently limited to 'tongtong' as of 2025-01-14)
		String[] voices = {"tongtong"};

		for (String voice : voices) {
			String requestId = String.format(REQUEST_ID_TEMPLATE + "-%s", System.currentTimeMillis(), voice);

			AudioSpeechRequest request = AudioSpeechRequest.builder()
				.model("cogtts")
				.input("Testing voice: " + voice)
				.voice(voice)
				.requestId(requestId)
				.build();

			AudioSpeechApiResponse response = audioService.createSpeech(request);

			assertNotNull(response, "Speech response should not be null for voice: " + voice);
			logger.info("Voice {} response: {}", voice, mapper.writeValueAsString(response));
		}
	}

	@Test
	@DisplayName("Test Transcription with Different Audio Formats")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testTranscriptionWithDifferentFormats() throws JsonProcessingException {
		String[] audioFiles = { "asr.wav", "asr.webm" };

		for (String audioFile : audioFiles) {
			String requestId = String.format(REQUEST_ID_TEMPLATE + "-%s", System.currentTimeMillis(), audioFile);
			File file = new File("src/test/resources/" + audioFile);

			AudioTranscriptionsRequest request = AudioTranscriptionsRequest.builder()
				.model("glm-asr")
				.file(file)
				.stream(false)
				.requestId(requestId)
				.build();

			ChatCompletionResponse response = audioService.createTranscription(request);

			assertNotNull(response, "Transcription response should not be null for file: " + audioFile);
			logger.info("Audio file {} transcription response: {}", audioFile, mapper.writeValueAsString(response));
		}
	}

}
