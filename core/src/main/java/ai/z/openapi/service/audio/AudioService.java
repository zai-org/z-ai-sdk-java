package ai.z.openapi.service.audio;

/**
 * Audio service interface
 */
public interface AudioService {

	/**
	 * Creates speech from text using text-to-speech.
	 * @param request the speech generation request
	 * @return AudioSpeechApiResponse containing the generated speech
	 */
	AudioSpeechResponse createSpeech(AudioSpeechRequest request);

	/**
	 * Creates customized speech with specific voice characteristics.
	 * @param request the speech customization request
	 * @return AudioCustomizationResponse containing the customized speech result
	 */
	AudioCustomizationResponse createCustomSpeech(AudioCustomizationRequest request);

	/**
	 * Creates audio transcription from audio files.
	 * @param request the transcription request
	 * @return AudioTranscriptionResponse containing the transcription result
	 */
	AudioTranscriptionResponse createTranscription(AudioTranscriptionRequest request);

}