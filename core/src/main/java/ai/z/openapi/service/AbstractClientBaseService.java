package ai.z.openapi.service;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.core.model.FlowableClientResponse;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.model.ResponseBodyCallback;
import ai.z.openapi.service.model.SSE;
import ai.z.openapi.service.model.ZAiError;
import ai.z.openapi.service.model.ZAiHttpException;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.RequestSupplier;
import ai.z.openapi.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.util.Objects;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;

/**
 * Abstract base service class that provides common functionality for API client
 * implementations. This class handles request execution, response processing, and error
 * handling for both synchronous and streaming API calls.
 */
public abstract class AbstractClientBaseService {

	protected final static Logger logger = LoggerFactory.getLogger(AbstractClientBaseService.class);

	protected static final ObjectMapper mapper = MessageDeserializeFactory.defaultObjectMapper();

	/**
	 * Executes a synchronous API request.
	 * @param <Data> the type of data returned by the API
	 * @param <Param> the type of parameters sent to the API
	 * @param <TReq> the type of the request object
	 * @param <TResp> the type of the response object
	 * @param request the request object containing parameters
	 * @param requestSupplier the supplier that creates the API call
	 * @param tRespClass the class of the response type
	 * @return the response object containing the API result
	 */
	public abstract <Data, Param, TReq extends ClientRequest<Param>, TResp extends ClientResponse<Data>> TResp executeRequest(
			TReq request, RequestSupplier<Param, Data> requestSupplier, Class<TResp> tRespClass);

	/**
	 * Executes a streaming API request that returns a continuous stream of data.
	 * @param <Data> the type of data returned by the API stream
	 * @param <Param> the type of parameters sent to the API
	 * @param <TReq> the type of the request object
	 * @param <TResp> the type of the streaming response object
	 * @param request the request object containing parameters
	 * @param requestSupplier the supplier that creates the streaming API call
	 * @param tRespClass the class of the response type
	 * @param tDataClass the class of the data type in the stream
	 * @return the streaming response object containing the API result stream
	 */
	public abstract <Data, Param, TReq extends ClientRequest<Param>, TResp extends FlowableClientResponse<Data>> TResp streamRequest(
			TReq request, FlowableRequestSupplier<Param, Call<ResponseBody>> requestSupplier, Class<TResp> tRespClass,
			Class<Data> tDataClass);

	/**
	 * Executes a Single API call synchronously and handles errors.
	 * @param <T> the type of the response
	 * @param apiCall the Single API call to execute
	 * @return the response from the API call
	 * @throws ZAiHttpException if an HTTP error occurs with a parseable error body
	 * @throws HttpException if an HTTP error occurs that cannot be parsed
	 */
	public static <T> T execute(Single<T> apiCall) {
		try {
			T response = apiCall.blockingGet();

			// Check status code if the response is a Response type object
			if (response instanceof Response) {
				handleResponse((Response<?>) response);
			}

			return response;
		}
		catch (HttpException e) {
			logger.error("HTTP exception: {}", e.getMessage());
			if (e.response() == null || e.response().errorBody() == null) {
				throw e;
			}
			try (ResponseBody responseBody = Objects.requireNonNull(e.response()).errorBody()) {
				if (responseBody == null) {
					throw e;
				}
				String errorBody = responseBody.string();
				if (StringUtils.isEmpty(errorBody)) {
					throw e;
				}
				ZAiError error = mapper.readValue(errorBody, ZAiError.class);
				throw new ZAiHttpException(error, e, e.code());
			}
			catch (IOException ex) {
				// couldn't parse ZAiError error
				throw e;
			}
		}
	}

	/**
	 * Handles HTTP response and throws exception if not successful.
	 * @param response the HTTP response to handle
	 * @throws HttpException if the response is not successful
	 */
	private static void handleResponse(Response<?> response) {
		if (!response.isSuccessful()) {
			throw new HttpException(response);
		}
	}

	/**
	 * Creates a streaming Flowable that maps SSE data to the specified class type.
	 * @param <T> the type to map the SSE data to
	 * @param apiCall the API call that returns streaming data
	 * @param cl the class to map the SSE data to
	 * @return a Flowable of the specified type
	 */
	public <T> Flowable<T> stream(retrofit2.Call<ResponseBody> apiCall, Class<T> cl) {
		return stream(apiCall).map(sse -> mapper.readValue(sse.getData(), cl));
	}

	/**
	 * Creates a streaming Flowable of SSE events without emitting done events.
	 * @param apiCall the API call that returns streaming data
	 * @return a Flowable of SSE events
	 */
	public static Flowable<SSE> stream(retrofit2.Call<ResponseBody> apiCall) {
		return stream(apiCall, false);
	}

	/**
	 * Creates a streaming Flowable of SSE events with optional done event emission.
	 * @param apiCall the API call that returns streaming data
	 * @param emitDone whether to emit done events
	 * @return a Flowable of SSE events
	 */
	public static Flowable<SSE> stream(retrofit2.Call<ResponseBody> apiCall, boolean emitDone) {
		return Flowable.create(emitter -> apiCall.enqueue(new ResponseBodyCallback(emitter, emitDone)),
				BackpressureStrategy.BUFFER);
	}

}
