package ai.z.openapi;

import ai.z.openapi.core.cache.ICache;
import ai.z.openapi.core.cache.LocalCache;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.core.token.TokenManager;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZaiClientTest {

	private final static Logger logger = LoggerFactory.getLogger(ZaiClientTest.class);

	private static final ZaiClient client;

	private static final ZaiConfig zaiConfig;

	static {
		zaiConfig = new ZaiConfig();
		String apiKey = zaiConfig.getApiKey();
		if (apiKey == null) {
			zaiConfig.setApiKey("test-api-key.test-api-secret");
		}
		client = new ZaiClient(zaiConfig);
	}

	@Test
	public void testTokenManager() {
		ZaiConfig zAiConfig = new ZaiConfig();
		zAiConfig.setApiKey("a.b");
		ICache cache = LocalCache.getInstance();
		TokenManager tokenManager = new TokenManager(cache);
		String token = tokenManager.getToken(zAiConfig);
		assert token != null;
	}

	@Test
	public void testTokenManagerCache() {
		ZaiConfig zAiConfig = new ZaiConfig();
		zAiConfig.setApiKey("a.b");
		String tokenCacheKey = String.format("%s-%s", "zai_oapi_token", zAiConfig.getApiKey());
		ICache cache = LocalCache.getInstance();
		TokenManager tokenManager = new TokenManager(cache);
		String token = tokenManager.getToken(zAiConfig);
		assert token != null;
		assert cache.get(tokenCacheKey) != null;
	}

}
