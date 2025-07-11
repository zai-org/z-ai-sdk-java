package ai.z.openapi.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Common request base class with extra JSON fields. Other ZAI request classes should
 * extend this class to inherit common request parameters.
 */

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class CommonRequest {

	/**
	 * Request ID provided by the client, must be unique. Used to distinguish each
	 * request. If not provided by the client, the platform will generate one by default.
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * A unique identifier representing your end-user, which will help ZAI to monitor and
	 * detect abuse.
	 */
	@JsonProperty("user_id")
	private String userId;

	private Map<String, Object> extraJson;

}
