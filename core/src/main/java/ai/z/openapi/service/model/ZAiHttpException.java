package ai.z.openapi.service.model;

public class ZAiHttpException extends RuntimeException {

	/**
	 * HTTP status code
	 */
	public final int statusCode;

	/**
	 * ZAI error code, for example "invalid_api_key"
	 */
	public final String code;

	public final String param;

	/**
	 * ZAI error type, for example "invalid_request_error"
	 */
	public final String type;

	public ZAiHttpException(ZAiError error, Exception parent, int statusCode) {
		super(error.error.message, parent);
		this.statusCode = statusCode;
		this.code = error.error.code;
		this.param = error.error.param;
		this.type = error.error.type;
	}

}
