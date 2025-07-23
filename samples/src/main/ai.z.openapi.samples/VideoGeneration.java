package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideosResponse;


public class VideoGeneration {
    public static void main(String[] args) throws InterruptedException {
        ZaiClient client = ZaiClient.builder().ofZHIPU()
                .apiKey("API_KEY")
                .build();

        VideoCreateParams request = VideoCreateParams.builder()
                .model("cogvideox-2")
                .imageUrl("https://aigc-files.bigmodel.cn/api/cogview/20250723213827da171a419b9b4906_0.png")
                .prompt("比得兔开小汽车，游走在马路上，脸上的表情充满开心喜悦。")
                .duration(14)
                .requestId("6746573568374y543")
                .size("720x480")
                .build();

        VideosResponse response = client.videos().videoGenerations(request);
        System.out.println(response.getData());
        // 等待 10 分钟 异步通过得到的任务ID 获取最终生成视频
        Thread.sleep(600000L);
        VideosResponse videosResponse = client.videos().videoGenerationsResult(response.getData().getId());
        System.out.println(videosResponse.getData().getVideoResult());
    }
}