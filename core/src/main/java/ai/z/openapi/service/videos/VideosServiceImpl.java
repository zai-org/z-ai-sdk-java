package ai.z.openapi.service.videos;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.api.videos.VideosApi;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.utils.RequestSupplier;

/**
 * Implementation of VideosService
 */
public class VideosServiceImpl implements VideosService {

	private final ZaiClient zAiClient;

	private final VideosApi videosApi;

	public VideosServiceImpl(ZaiClient client) {
		this.zAiClient = client;
		this.videosApi = client.retrofit().create(VideosApi.class);
	}

	@Override
	public VideosResponse videoGenerations(VideoCreateParams request) {
		RequestSupplier<VideoCreateParams, VideoObject> supplier = videosApi::videoGenerations;
		return zAiClient.executeRequest(request, supplier, VideosResponse.class);
	}

	@Override
	public VideosResponse videoGenerationsResult(String taskId) {
		AsyncResultRetrieveParams request = new AsyncResultRetrieveParams(taskId);
		RequestSupplier<AsyncResultRetrieveParams, VideoObject> supplier = (params) -> videosApi
			.videoGenerationsResult(params.getTaskId());
		return zAiClient.executeRequest(request, supplier, VideosResponse.class);
	}

}