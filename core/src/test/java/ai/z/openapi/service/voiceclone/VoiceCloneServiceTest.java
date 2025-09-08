package ai.z.openapi.service.voiceclone;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.service.file.FileApiResponse;
import ai.z.openapi.service.file.FileService;
import ai.z.openapi.service.file.FileUploadParams;
import ai.z.openapi.service.file.UploadFilePurpose;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for VoiceCloneService functionality. Tests voice cloning operations
 * including file upload, voice creation, deletion, and listing. Requires ZAI_API_KEY
 * environment variable to be set for integration tests.
 */
@DisplayName("VoiceCloneService Tests")
public class VoiceCloneServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(VoiceCloneServiceTest.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private VoiceCloneService voiceCloneService;

	private FileService fileService;

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		voiceCloneService = client.voiceClone();
		fileService = client.files();
	}

	@Test
	@DisplayName("Test voice clone creation with file upload")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testCloneVoice() throws JsonProcessingException {
		// Step 1: Upload the voice input audio file
		// First, we need to upload the voice sample audio file to get a file ID
		String voiceInputFilePath = "src/test/resources/voice_clone_input.mp3";

		FileUploadParams uploadRequest = FileUploadParams.builder()
			.filePath(voiceInputFilePath)
			.purpose(UploadFilePurpose.VOICE_CLONE_INPUT.value())
			.requestId("voice-clone-test-" + System.currentTimeMillis())
			.build();

		FileApiResponse uploadResponse = fileService.uploadFile(uploadRequest);
		assertNotNull(uploadResponse, "File upload response should not be null");
		assertTrue(uploadResponse.isSuccess(), "File upload should be successful");
		assertNotNull(uploadResponse.getData(), "Uploaded file data should not be null");

		String fileId = uploadResponse.getData().getId();
		logger.info("Voice input file uploaded successfully with ID: {}", fileId);

		// Step 2: Create voice clone using the uploaded file
		// Now we can use the uploaded file ID to create a voice clone
		VoiceCloneRequest request = new VoiceCloneRequest();
		request.setVoiceName("Test");
		request.setText("This is sample text for voice cloning training");
		request.setInput("This is target text for voice preview generation");
		request.setFileId(fileId); // Use the actual file ID from upload
		request.setModel("CogTTS-clone");

		// Execute voice cloning
		VoiceCloneResponse response = voiceCloneService.cloneVoice(request);

		// Verify the voice cloning response
		assertNotNull(response, "Voice clone response should not be null");
		assertTrue(response.isSuccess(), "Voice cloning should be successful");
		assertNotNull(response.getData(), "Voice clone data should not be null");
		logger.info("Voice clone created successfully: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test voice list")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testListVoice() throws JsonProcessingException {
		// Create a voice list request with filtering parameters
		// This will retrieve voices filtered by type and name pattern
		VoiceListRequest request = new VoiceListRequest();
		request.setVoiceType("PRIVATE"); // Filter for custom voice clones
		request.setVoiceName("Test"); // Filter by voice name pattern

		// Execute the voice listing
		VoiceListResponse response = voiceCloneService.listVoice(request);

		// Verify the listing response
		logger.info("Voice list response: {}", mapper.writeValueAsString(response));
		assertNotNull(response, "Voice list response should not be null");
		assertTrue(response.isSuccess(), "Voice listing should be successful");
		assertNotNull(response.getData(), "Voice list data should not be null");
		logger.info("Voice list retrieved successfully: {}", mapper.writeValueAsString(response));
	}

	@Test
	@DisplayName("Test voice deletion")
	@EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
	void testDeleteVoice() throws JsonProcessingException {
		// Create a voice deletion request with a test voice
		// In a real scenario, this would be an ID from a previously created voice clone
		VoiceDeleteRequest request = new VoiceDeleteRequest();
		request.setVoice("Test voice");

		// Execute the voice deletion
		VoiceDeleteResponse response = voiceCloneService.deleteVoice(request);

		// Verify the deletion response
		assertNotNull(response, "Voice deletion response should not be null");
		assertTrue(response.isSuccess(), "Voice deletion should be successful");
		assertNotNull(response.getData(), "Voice deletion data should not be null");
		logger.info("Voice deleted successfully: {}", mapper.writeValueAsString(response));
	}

}