package ai.z.openapi.service.voiceclone;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.config.ZaiConfig;
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
 * VoiceCloneService test class
 */
@DisplayName("VoiceCloneService Tests")
public class VoiceCloneServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(VoiceCloneServiceTest.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private VoiceCloneService voiceCloneService;

    @BeforeEach
    void setUp() {
        ZaiConfig zaiConfig = new ZaiConfig();
        String apiKey = zaiConfig.getApiKey();
        if (apiKey == null) {
            zaiConfig.setApiKey("id.test-api-key");
        }
        ZaiClient client = new ZaiClient(zaiConfig);
        voiceCloneService = client.voiceClone();
    }

    @Test
    @DisplayName("Test voice clone creation")
    @EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
    void testCloneVoice() throws JsonProcessingException {
        // upload your voice input
        VoiceCloneRequest request = new VoiceCloneRequest();
        request.setVoiceName("Test Voice");
        request.setVoiceTextInput("This is sample text for voice cloning");
        request.setVoiceTextOutput("This is target text for preview");
        request.setFileId("test-file-id-123");

        VoiceCloneResponse response = voiceCloneService.cloneVoice(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        logger.info("Clone voice response: {}", mapper.writeValueAsString(response));
    }

    @Test
    @DisplayName("Test voice deletion")
    @EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
    void testDeleteVoice() throws JsonProcessingException {
        VoiceDeleteRequest request = new VoiceDeleteRequest();
        request.setVoiceId("test-voice-id-123");

        VoiceDeleteResponse response = voiceCloneService.deleteVoice(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        logger.info("Delete voice response: {}", mapper.writeValueAsString(response));
    }

    @Test
    @DisplayName("Test voice list")
    @EnabledIfEnvironmentVariable(named = "ZAI_API_KEY", matches = "^[^.]+\\.[^.]+$")
    void testListVoice() throws JsonProcessingException {
        VoiceListRequest request = new VoiceListRequest();
        request.setVoiceType("custom");
        request.setVoiceName("Test");

        VoiceListResponse response = voiceCloneService.listVoice(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        logger.info("List voice response: {}", mapper.writeValueAsString(response));
    }

}