package ai.z.openapi.api.audio;

import ai.z.openapi.service.audio.AudioSpeechRequest;
import ai.z.openapi.service.model.ModelData;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;

import java.util.Map;

public interface AudioApi {

	/**
	 * TTS interface (Text to speech)
	 * @param request
	 * @return
	 */
	@POST("audio/speech")
	Single<ResponseBody> audioSpeech(@Body AudioSpeechRequest request);

	/**
	 * Voice cloning interface
	 * @param request
	 * @return
	 */
	@Multipart
	@POST("audio/customization")
	Single<ResponseBody> audioCustomization(@PartMap Map<String, RequestBody> request,
			@Part MultipartBody.Part voiceData);

	@Streaming
	@POST("audio/transcriptions")
	@Multipart
	Call<ResponseBody> audioTranscriptionsStream(@PartMap Map<String, RequestBody> request,
			@Part MultipartBody.Part file);

	@POST("audio/transcriptions")
	@Multipart
	Single<ModelData> audioTranscriptions(@PartMap Map<String, RequestBody> request, @Part MultipartBody.Part file);

}
