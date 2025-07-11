package ai.z.openapi;

import ai.z.openapi.core.config.ZAiConfig;
import ai.z.openapi.service.model.SensitiveWordCheckRequest;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideosResponse;
import ai.z.openapi.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;


@Testcontainers
public class TestVideosClientApiService {
    private final static Logger logger = LoggerFactory.getLogger(TestVideosClientApiService.class);
    private static final ZAiConfig zaiConfig;
    private static final ZAiClient client;
    
    static {
        zaiConfig = new ZAiConfig();
        String apiKey = zaiConfig.getApiKey();
        if (apiKey == null) {
            zaiConfig.setApiKey("test-api-key.test-api-secret");
        }
        client = new ZAiClient(zaiConfig);
    }

    @Test
    public void testVideo(){

        VideoCreateParams build = VideoCreateParams.builder()
                .prompt("A person driving a boat")
                .model("cogvideox")
                .withAudio(Boolean.TRUE)
                .quality("quality")
                .build();
        VideosResponse apply = client.videos().videoGenerations(build);

        logger.info("apply:{}",apply);
    }

    @Test
    public void testVideoSensitiveWordCheck(){
        SensitiveWordCheckRequest sensitiveWordCheckRequest = SensitiveWordCheckRequest.builder()
                .type("ALL")
                .status("DISABLE")
                .build();

        VideoCreateParams build = VideoCreateParams.builder()
                .prompt("A person driving a boat")
                .model("cogvideox")
                .sensitiveWordCheck(sensitiveWordCheckRequest)
                .build();
        VideosResponse apply = client.videos().videoGenerations(build);

        logger.info("apply:{}",apply);
    }

    @Test
    public void testVideoByImage() throws IOException {
        Base64.Encoder encoder = Base64.getEncoder();
        String file = ClassLoader.getSystemResource("20.png").getFile();
        byte[] bytes = FileUtils.readFileToByteArray(new File(file));
        String imageUrl  = encoder.encodeToString( bytes);
        VideoCreateParams build = VideoCreateParams.builder()
                .prompt(
                          "This scene depicts a magical atmosphere. On a stone table surrounded by green plants and orange flowers, an ancient book lies open, its pages seemingly just turned. Next to the book sits a transparent magic orb filled with twinkling lights and flowing shadows, as if mysterious energy flows within. The base beneath the magic orb emits a faint glow, while tiny light particles float in the surrounding air, enhancing the scene's mystique and magical ambiance. Vague architectural structures can be seen in the background, further emphasizing the fantasy and mystery of this scene.")
                .imageUrl(imageUrl)
                .model("cogvideox")
                .build();
        VideosResponse apply = client.videos().videoGenerations(build);

        logger.info("apply:{}",apply);
    }

    @Test
    public void testVideoGenerationsResult(){

        VideoCreateParams build = VideoCreateParams.builder()

                .id("1000088872446167827091899")
                .build();
        VideosResponse apply = client.videos().videoGenerationsResult(build.getId());

        logger.info("apply:{}",apply);
    }
}
