package ai.z.openapi.service.videos;

import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideosResponse;

/**
 * Videos service interface
 */
public interface VideosService {
    
    /**
     * Creates video generations.
     * 
     * @param request the video generation request
     * @return VideosResponse containing the generation result
     */
    VideosResponse videoGenerations(VideoCreateParams request);
    
    /**
     * Retrieves video generation result.
     * 
     * @param taskId the task ID to retrieve
     * @return VideosResponse containing the generation result
     */
    VideosResponse videoGenerationsResult(String taskId);
}