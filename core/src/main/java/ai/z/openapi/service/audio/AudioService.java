package ai.z.openapi.service.audio;

import ai.z.openapi.service.model.ChatCompletionResponse;

/**
 * Audio service interface
 */
public interface AudioService {

	/**
	 * Creates speech from text using text-to-speech.
	 * @param request the speech generation request
	 * @return AudioSpeechApiResponse containing the generated speech
	 */
	AudioSpeechApiResponse createSpeech(AudioSpeechRequest request);

	/**
	 * Creates customized speech with specific voice characteristics.
	 * @param request the speech customization request
	 * @return AudioCustomizationApiResponse containing the customized speech result
	 */
	AudioCustomizationApiResponse createCustomSpeech(AudioCustomizationRequest request);

	/**
	 * Creates audio transcriptions from audio files.
	 * @param request the transcription request
	 * @return ChatCompletionResponse containing the transcription result
	 */
	ChatCompletionResponse createTranscription(AudioTranscriptionsRequest request);

}