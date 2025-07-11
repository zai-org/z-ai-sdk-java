package ai.z.openapi.service.image;

/**
 * Image service interface
 */
public interface ImageService {

	/**
	 * Creates an image based on the provided generation request.
	 * @param createImageRequest the image generation request
	 * @return ImageResponse containing the generated image result
	 */
	ImageResponse createImage(CreateImageRequest createImageRequest);

}