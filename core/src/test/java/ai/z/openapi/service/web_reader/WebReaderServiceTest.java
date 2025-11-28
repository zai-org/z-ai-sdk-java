package ai.z.openapi.service.web_reader;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.core.config.ZaiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WebReaderService Tests")
public class WebReaderServiceTest {

	private WebReaderService webReaderService;

	@BeforeEach
	void setUp() {
		ZaiConfig zaiConfig = new ZaiConfig();
		if (zaiConfig.getApiKey() == null) {
			zaiConfig.setApiKey("id.test-api-key");
		}
		ZaiClient client = new ZaiClient(zaiConfig);
		webReaderService = client.webReader();
	}

	@Test
	@DisplayName("Test WebReaderService Instantiation")
	void testWebReaderServiceInstantiation() {
		assertNotNull(webReaderService, "WebReaderService should be instantiated");
	}

}