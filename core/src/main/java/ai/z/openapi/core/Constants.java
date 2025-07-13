package ai.z.openapi.core;

/**
 * Constants class containing all the configuration values and model identifiers used
 * throughout the Z.AI OpenAPI SDK.
 *
 * This class provides centralized access to: - API base URLs - Model identifiers for
 * different AI capabilities - Invocation method constants
 *
 */
public final class Constants {

	// Private constructor to prevent instantiation
	private Constants() {
		throw new UnsupportedOperationException("Constants class cannot be instantiated");
	}

	// =============================================================================
	// API Configuration
	// =============================================================================

	/**
	 * Base URL for the ZHIPU AI OpenAPI service. All API requests will be made to
	 * endpoints under this base URL.
	 */
	public static final String ZHIPU_AI_BASE_URL = "https://open.bigmodel.cn/api/paas/v4/";

	/**
	 * Base URL for the Z.AI OpenAPI service. All API requests will be made to endpoints
	 * under this base URL.
	 */
	public static final String Z_AI_BASE_URL = "https://api.z.ai/api/paas/v4/";

	// =============================================================================
	// Text Generation Models
	// =============================================================================

	/**
	 * GLM-4 Plus model - Enhanced version with improved capabilities.
	 */
	public static final String ModelChatGLM4Plus = "glm-4-plus";

	/**
	 * GLM-4 Air model - Lightweight version optimized for speed.
	 */
	public static final String ModelChatGLM4Air = "glm-4-air";

	/**
	 * GLM-4 Flash model - Ultra-fast response model.
	 */
	public static final String ModelChatGLM4Flash = "glm-4-flash";

	/**
	 * GLM-4 standard model - Balanced performance and capability.
	 */
	public static final String ModelChatGLM4 = "glm-4";

	/**
	 * GLM-4 model version 0520 - Specific version release.
	 */
	public static final String ModelChatGLM40520 = "glm-4-0520";

	/**
	 * GLM-4 AirX model - Extended Air model with additional features.
	 */
	public static final String ModelChatGLM4Airx = "glm-4-airx";

	/**
	 * GLM-4 Long model - Optimized for long-context conversations.
	 */
	public static final String ModelChatGLMLong = "glm-4-long";

	/**
	 * GLM-4 Voice model - Specialized for voice-related tasks.
	 */
	public static final String ModelChatGLM4Voice = "glm-4-voice";

	// =============================================================================
	// Vision Models (Image Understanding)
	// =============================================================================

	/**
	 * GLM-4V Plus model - Enhanced vision model for image understanding.
	 */
	public static final String ModelChatGLM4VPlus = "glm-4v-plus";

	/**
	 * GLM-4V standard model - Standard vision model for image analysis.
	 */
	public static final String ModelChatGLM4V = "glm-4v";

	// =============================================================================
	// Image Generation Models
	// =============================================================================

	/**
	 * CogView-3 Plus model - Enhanced image generation capabilities.
	 */
	public static final String ModelCogView3Plus = "cogview-3-plus";

	/**
	 * CogView-3 standard model - Standard image generation model.
	 */
	public static final String ModelCogView = "cogview-3";

	// =============================================================================
	// Embedding Models
	// =============================================================================

	/**
	 * Embedding model version 2 - Text embedding generation.
	 */
	public static final String ModelEmbedding2 = "embedding-2";

	/**
	 * Embedding model version 3 - Latest text embedding generation.
	 */
	public static final String ModelEmbedding3 = "embedding-3";

	// =============================================================================
	// Specialized Models
	// =============================================================================

	/**
	 * CharGLM-3 model - Anthropomorphic character interaction model.
	 */
	public static final String ModelCharGLM3 = "charglm-3";

	/**
	 * CogTTS model - Text-to-Speech synthesis model.
	 */
	public static final String ModelTTS = "cogtts";

	// =============================================================================
	// API Invocation Methods
	// =============================================================================

	/**
	 * Asynchronous invocation method - For non-blocking API calls.
	 */
	public static final String INVOKE_METHOD_ASYNC = "async-invoke";

	/**
	 * Server-Sent Events invocation method - For streaming responses.
	 */
	public static final String INVOKE_METHOD_SSE = "sse-invoke";

	/**
	 * Standard synchronous invocation method - For blocking API calls.
	 */
	public static final String INVOKE_METHOD = "invoke";

}
