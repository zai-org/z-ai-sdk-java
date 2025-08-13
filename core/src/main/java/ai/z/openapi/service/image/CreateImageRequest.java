package ai.z.openapi.service.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import ai.z.openapi.service.model.SensitiveWordCheckRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * A request for ZAi to create an image based on a prompt All fields except prompt are
 * optional
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateImageRequest extends CommonRequest implements ClientRequest<CreateImageRequest> {

	/**
	 * A text description of the desired image(s). The maximum length is 1000 characters
	 * for dall-e-2 and 4000 characters for dall-e-3.
	 */
	private String prompt;

	/**
	 * The model to use for image generation. Defaults to "dall-e-2".
	 */
	private String model;

	/**
	 * The size of the image to generate. Defaults to "256x256".
	 */
	private String size;

	/**
	 * Sensitive word detection control
	 */
	@JsonProperty("sensitive_word_check")
	private SensitiveWordCheckRequest sensitiveWordCheck;

	/**
	 * Forced watermark switch
	 */
	@JsonProperty("watermark_enabled")
	private Boolean watermarkEnabled;

}
