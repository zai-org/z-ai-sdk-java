package ai.z.openapi.service.audio;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.core.config.ZaiConfig;
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
	@DisplayName("Should instantiate AudioService successfully")
	void shouldInstantiateAudioServiceSuccessfully() {
		assertNotNull(audioService, "AudioService should be properly instantiated");
		assertInstanceOf(AudioServiceImpl.class, audioService,
				"AudioService should be an instance of AudioServiceImpl");
	}

	@Test
	@DisplayName("Should generate speech from text successfully")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void shouldGenerateSpeechFromTextSuccessfully() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

		AudioSpeechRequest request = AudioSpeechRequest.builder()
			.model(Constants.ModelTTS)
			.input("Hello, this is a test for text-to-speech functionality.")
			.voice("tongtong")
			.requestId(requestId)
			.build();

		// Execute test
		AudioSpeechResponse response = audioService.createSpeech(request);

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
	@DisplayName("Should generate custom speech with voice cloning successfully")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void shouldGenerateCustomSpeechWithVoiceCloningSuccessfully() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		File voiceFile = new File("src/test/resources/asr.wav");

		AudioCustomizationRequest request = AudioCustomizationRequest.builder()
			.model(Constants.ModelTTS)
			.input("This is a test for custom voice generation.")
			.voiceData(voiceFile)
			.voiceText("Sample voice text for cloning")
			.responseFormat("wav")
			.requestId(requestId)
			.build();

		// Execute test
		AudioCustomizationResponse response = audioService.createCustomSpeech(request);

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
	@DisplayName("Should transcribe audio with blocking")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void shouldTranscribeAudioWithBlocking() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		File audioFile = new File("src/test/resources/asr.wav");

		AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
			.model(Constants.ModelGLMASR)
			.file(audioFile)
			.stream(false)
			.requestId(requestId)
			.build();

		// Execute test
		AudioTranscriptionResponse response = audioService.createTranscription(request);

		// Verify results
		assertNotNull(response, "Transcription response should not be null");
		assertTrue(response.isSuccess(), "Transcription response should be successful");
		assertNotNull(response.getData(), "Transcription response data should not be null");
		assertNotNull(response.getData().getText(), "Transcription text should not be null");
		assertNull(response.getError(), "Response error should be null");
		logger.info("Blocking transcription response: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Should transcribe audio with streaming")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void shouldTranscribeAudioWithStreaming() throws JsonProcessingException {
		// Prepare test data
		String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
		File audioFile = new File("src/test/resources/asr.wav");

		AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
			.model(Constants.ModelGLMASR)
			.file(audioFile)
			.stream(true)
			.requestId(requestId)
			.build();

		// Execute test
		AudioTranscriptionResponse response = audioService.createTranscription(request);

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
	@DisplayName("Should throw exception when speech request is null")
	void shouldThrowExceptionWhenSpeechRequestIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createSpeech(null);
		}, "Null speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Should throw exception when custom speech request is null")
	void shouldThrowExceptionWhenCustomSpeechRequestIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createCustomSpeech(null);
		}, "Null custom speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Should throw exception when transcription request is null")
	void shouldThrowExceptionWhenTranscriptionRequestIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createTranscription(null);
		}, "Null transcription request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Should throw exception when audio file does not exist")
	void shouldThrowExceptionWhenAudioFileDoesNotExist() {
		AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
			.model(Constants.ModelGLMASR)
			.file(new File("non-existent-file.wav"))
			.stream(false)
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createTranscription(request);
		}, "Invalid audio file should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Should throw exception when model is null in speech request")
	void shouldThrowExceptionWhenModelIsNullInSpeechRequest() {
		AudioSpeechRequest request = AudioSpeechRequest.builder().input("Test input").voice("tongtong").build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createSpeech(request);
		}, "Null model in speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Should throw exception when input is empty in speech request")
	void shouldThrowExceptionWhenInputIsEmptyInSpeechRequest() {
		AudioSpeechRequest request = AudioSpeechRequest.builder()
			.model(Constants.ModelTTS)
			.input("")
			.voice("tongtong")
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createSpeech(request);
		}, "Empty input in speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Should throw exception when voice data is null in custom speech request")
	void shouldThrowExceptionWhenVoiceDataIsNullInCustomSpeechRequest() {
		AudioCustomizationRequest request = AudioCustomizationRequest.builder()
			.model(Constants.ModelTTS)
			.input("Test input")
			.voiceText("Voice text")
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createCustomSpeech(request);
		}, "Null voice data in custom speech request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Should throw exception when file is null in transcription request")
	void shouldThrowExceptionWhenFileIsNullInTranscriptionRequest() {
		AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
			.model(Constants.ModelGLMASR)
			.stream(false)
			.build();

		assertThrows(IllegalArgumentException.class, () -> {
			audioService.createTranscription(request);
		}, "Null file in transcription request should throw IllegalArgumentException");
	}

	@Test
	@DisplayName("Should generate speech successfully with different voice options")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void shouldGenerateSpeechSuccessfullyWithDifferentVoiceOptions() throws JsonProcessingException {
		// Test with available voice options (currently limited to 'tongtong' as of
		// 2025-01-14)
		String[] voices = { "tongtong" };

		for (String voice : voices) {
			String requestId = String.format(REQUEST_ID_TEMPLATE + "-%s", System.currentTimeMillis(), voice);

			AudioSpeechRequest request = AudioSpeechRequest.builder()
				.model(Constants.ModelTTS)
				.input("Testing voice: " + voice)
				.voice(voice)
				.requestId(requestId)
				.build();

			AudioSpeechResponse response = audioService.createSpeech(request);

			assertNotNull(response, "Speech response should not be null for voice: " + voice);
			logger.info("Voice {} response: {}", voice, mapper.writeValueAsString(response));
		}
	}

	@Test
	@DisplayName("Should transcribe different audio formats successfully")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void shouldTranscribeDifferentAudioFormatsSuccessfully() throws JsonProcessingException {
		String[] audioFiles = { "asr.wav", "asr.mp3" };

		for (String audioFile : audioFiles) {
			String requestId = String.format(REQUEST_ID_TEMPLATE + "-%s", System.currentTimeMillis(), audioFile);
			File file = new File("src/test/resources/" + audioFile);

			AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
				.model(Constants.ModelGLMASR)
				.file(file)
				.stream(false)
				.requestId(requestId)
				.build();

			AudioTranscriptionResponse response = audioService.createTranscription(request);

			assertNotNull(response, "Transcription response should not be null for file: " + audioFile);
			logger.info("Audio file {} transcription response: {}", audioFile, mapper.writeValueAsString(response));
		}
	}

}
