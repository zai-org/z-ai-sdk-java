package ai.z.openapi.service.audio;

import ai.z.openapi.ZAiClient;
import ai.z.openapi.api.audio.AudioApi;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.RequestSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Audio service implementation
 */
@Slf4j
public class AudioServiceImpl implements AudioService {

    protected static final ObjectMapper mapper = MessageDeserializeFactory.defaultObjectMapper();
    private final ZAiClient zAiClient;
    private final AudioApi audioApi;

    public AudioServiceImpl(ZAiClient zAiClient) {
        this.zAiClient = zAiClient;
        this.audioApi = zAiClient.retrofit().create(AudioApi.class);
    }
    
    @Override
    public AudioSpeechApiResponse createSpeech(AudioSpeechRequest request) {
        RequestSupplier<AudioSpeechRequest, java.io.File> supplier = (params) -> {
            try {
                Single<ResponseBody> responseBody = audioApi.audioSpeech(params);
                Path tempDirectory = Files.createTempFile("audio_speech" + UUID.randomUUID(),".wav");
                java.io.File file = tempDirectory.toFile();
                writeResponseBodyToFile(responseBody.blockingGet(), file);
                return Single.just(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        return this.zAiClient.executeRequest(request, supplier, AudioSpeechApiResponse.class);
    }
    
    @Override
    public AudioCustomizationApiResponse createCustomSpeech(AudioCustomizationRequest request) {
        RequestSupplier<AudioCustomizationRequest, java.io.File> supplier = (params) -> {
            try {
                java.io.File voiceFile = params.getVoiceData();
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), voiceFile);
                MultipartBody.Part voiceData = MultipartBody.Part.createFormData("voice_data", voiceFile.getName(), requestFile);

                Map<String, RequestBody> requestMap = new HashMap<>();
                if (params.getInput() != null) {
                    requestMap.put("input", RequestBody.create(MediaType.parse("text/plain"), params.getInput()));
                }
                if (params.getModel() != null) {
                    requestMap.put("model", RequestBody.create(MediaType.parse("text/plain"), params.getModel()));
                }
                if (params.getVoiceText() != null) {
                    requestMap.put("voice_text", RequestBody.create(MediaType.parse("text/plain"), params.getVoiceText()));
                }
                if (params.getResponseFormat() != null) {
                    requestMap.put("response_format", RequestBody.create(MediaType.parse("text/plain"), params.getResponseFormat()));
                }
                if (params.getSensitiveWordCheck() != null) {
                    try {
                        String sensitiveWordCheckJson = mapper.writeValueAsString(params.getSensitiveWordCheck());
                        requestMap.put("sensitive_word_check", RequestBody.create(MediaType.parse("application/json"), sensitiveWordCheckJson));
                    } catch (Exception e) {
                        log.error("Error serializing sensitive_word_check: {}", e.getMessage(), e);
                    }
                }
                if (params.getRequestId() != null) {
                    requestMap.put("request_id", RequestBody.create(MediaType.parse("text/plain"), params.getRequestId()));
                }
                if (params.getUserId() != null) {
                    requestMap.put("user_id", RequestBody.create(MediaType.parse("text/plain"), params.getUserId()));
                }

                Single<ResponseBody> responseBody = audioApi.audioCustomization(requestMap,voiceData);
                Path tempDirectory = Files.createTempFile("audio_customization" + UUID.randomUUID(),".wav");
                java.io.File file = tempDirectory.toFile();
                writeResponseBodyToFile(responseBody.blockingGet(), file);
                return Single.just(file);
            } catch (IOException e) {
                log.error("Error create custom speak: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        };
        return this.zAiClient.executeRequest(request, supplier, AudioCustomizationApiResponse.class);
    }
    
    @Override
    public ChatCompletionResponse createTranscription(AudioTranscriptionsRequest request) {
        if (request.getStream()) {
            return createTranscriptionStream(request);
        } else {
            return createTranscriptionSync(request);
        }
    }
    
    private ChatCompletionResponse createTranscriptionStream(AudioTranscriptionsRequest request) {
        FlowableRequestSupplier<AudioTranscriptionsRequest, retrofit2.Call<ResponseBody>> supplier = params -> {
            try {
                java.io.File file = params.getFile();
                Tika tika = new Tika();
                String contentType = tika.detect(file);
                RequestBody requestFile = RequestBody.create(MediaType.parse(contentType), file);
                MultipartBody.Part fileData = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                Map<String, RequestBody> requestMap = new HashMap<>();
                if (params.getModel() != null) {
                    requestMap.put("model", RequestBody.create(MediaType.parse("text/plain"), params.getModel()));
                }
                if (params.getStream() != null) {
                    requestMap.put("stream", RequestBody.create(MediaType.parse("text/plain"), params.getStream().toString()));
                }
                if (params.getRequestId() != null) {
                    requestMap.put("request_id", RequestBody.create(MediaType.parse("text/plain"), params.getRequestId()));
                }
                if (params.getUserId() != null) {
                    requestMap.put("user_id", RequestBody.create(MediaType.parse("text/plain"), params.getUserId()));
                }

                return audioApi.audioTranscriptionsStream(requestMap, fileData);
            } catch (IOException e) {
                log.error("Error create transcription: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        };
        return this.zAiClient.streamRequest(
                request,
                supplier,
                ChatCompletionResponse.class,
                ModelData.class
        );
    }
    
    private ChatCompletionResponse createTranscriptionSync(AudioTranscriptionsRequest request) {
        RequestSupplier<AudioTranscriptionsRequest, ModelData> supplier = (params) -> {
            try {
                java.io.File file = params.getFile();
                Tika tika = new Tika();
                String contentType = tika.detect(file);
                RequestBody requestFile = RequestBody.create(MediaType.parse(contentType), file);
                MultipartBody.Part fileData = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                Map<String, RequestBody> requestMap = new HashMap<>();
                if (params.getModel() != null) {
                    requestMap.put("model", RequestBody.create(MediaType.parse("text/plain"), params.getModel()));
                }
                if (params.getStream() != null) {
                    requestMap.put("stream", RequestBody.create(MediaType.parse("text/plain"), params.getStream().toString()));
                }
                if (params.getRequestId() != null) {
                    requestMap.put("request_id", RequestBody.create(MediaType.parse("text/plain"), params.getRequestId()));
                }
                if (params.getUserId() != null) {
                    requestMap.put("user_id", RequestBody.create(MediaType.parse("text/plain"), params.getUserId()));
                }

                return audioApi.audioTranscriptions(requestMap, fileData);
            } catch (IOException e) {
                log.error("Error create transcription: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        };
        return this.zAiClient.executeRequest(request, supplier, ChatCompletionResponse.class);
    }

    private void writeResponseBodyToFile(ResponseBody body, java.io.File file) {
        try (InputStream inputStream = body.byteStream();
             OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("writeResponseBodyToFile error,msg:{}",e.getMessage(),e);
        }
    }
}