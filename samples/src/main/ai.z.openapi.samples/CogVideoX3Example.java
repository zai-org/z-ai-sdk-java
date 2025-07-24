package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideoObject;
import ai.z.openapi.service.videos.VideosResponse;

import java.util.Arrays;

/**
 * CogVideoX-3 Example
 * Demonstrates how to use ZaiClient for advanced video generation with CogVideoX-3
 * Features: Text-to-video, Image-to-video, First-last frame generation
 */
public class CogVideoX3Example {
    
    public static void main(String[] args) {
        // Create client, recommended to set API Key via environment variable
        // export ZAI_API_KEY=your.api.key
        ZaiClient client = ZaiClient.builder().ofZHIPU().build();
        
        // Or set API Key via code
        // ZaiClient client = ZaiClient.builder().ofZHIPU()
        //         .apiKey("your_api_key")
        //         .build();
        
        // Video generation examples
        textToVideoExample(client);
        imageToVideoExample(client);
        firstLastFrameVideoExample(client);
    }
    
    /**
     * Example of text-to-video generation using CogVideoX-3
     */
    private static void textToVideoExample(ZaiClient client) {
        System.out.println("=== CogVideoX-3 文本生成视频示例 ===");
        
        try {
            VideoCreateParams request = VideoCreateParams.builder()
                .model("cogvideox-3")
                .prompt("一只可爱的小猫在花园里追逐蝴蝶，阳光明媚，花朵盛开，画面清晰稳定")
                .quality("quality")  // "quality" for quality priority, "speed" for speed priority
                .withAudio(true)
                .size("1920x1080")  // Video resolution, supports up to 4K
                .fps(30)  // Frame rate, can be 30 or 60
                .build();
            
            VideosResponse response = client.videos().videoGenerations(request);
            
            if (response.isSuccess()) {
                String taskId = response.getData().getId();
                System.out.println("视频生成任务已提交，任务ID: " + taskId);
                System.out.println("请等待视频生成完成...");
                
                // Wait and check result
                Thread.sleep(60000); // Wait 1 minute
                checkVideoResult(client, taskId);
            } else {
                System.err.println("错误: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of image-to-video generation using CogVideoX-3
     */
    private static void imageToVideoExample(ZaiClient client) {
        System.out.println("\n=== CogVideoX-3 图像生成视频示例 ===");
        
        try {
            VideoCreateParams request = VideoCreateParams.builder()
                .model("cogvideox-3")
                .imageUrl("https://img.iplaysoft.com/wp-content/uploads/2019/free-images/free_stock_photo.jpg")
                .prompt("让画面动起来，展现自然的动态效果")
                .quality("quality")
                .withAudio(true)
                .size("1920x1080")
                .fps(30)
                .build();

            VideosResponse response = client.videos().videoGenerations(request);
            
            if (response.isSuccess()) {
                String taskId = response.getData().getId();
                System.out.println("图生视频任务已提交，任务ID: " + taskId);
                System.out.println("请等待视频生成完成...");
                
                // Wait and check result
                Thread.sleep(60000); // Wait 1 minute
                checkVideoResult(client, taskId);
            } else {
                System.err.println("错误: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example of first-last frame video generation using CogVideoX-3
     * This is a new feature in CogVideoX-3
     */
    private static void firstLastFrameVideoExample(ZaiClient client) {
        System.out.println("\n=== CogVideoX-3 首尾帧视频生成示例 ===");
        
        try {
            // Define first and last frame URLs
            String firstFrameUrl = "https://gd-hbimg.huaban.com/ccee58d77afe8f5e17a572246b1994f7e027657fe9e6-qD66In_fw1200webp";
            String lastFrameUrl = "https://gd-hbimg.huaban.com/cc2601d568a72d18d90b2cc7f1065b16b2d693f7fa3f7-hDAwNq_fw1200webp";

            VideoCreateParams request = VideoCreateParams.builder()
                .model("cogvideox-3")
                .imageUrl(Arrays.asList(firstFrameUrl, lastFrameUrl))  // Pass first and last frame URLs
                .prompt("龙王转成敖丙，水墨风晕染，主体转体缓缓变身，突出变身细节，旋转运镜，过渡丝滑、流畅自然")
                .quality("quality")
                .withAudio(true)
                .size("1920x1080")
                .fps(30)
                .build();

            VideosResponse response = client.videos().videoGenerations(request);
            
            if (response.isSuccess()) {
                String taskId = response.getData().getId();
                System.out.println("首尾帧视频生成任务已提交，任务ID: " + taskId);
                System.out.println("请等待视频生成完成...");
                System.out.println("Note: 首尾帧生成功能可以创建连贯的转场视频，让静态帧自然衔接为动态叙事。");
                
                // Wait and check result
                Thread.sleep(60000); // Wait 1 minute
                checkVideoResult(client, taskId);
            } else {
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check video generation result
     */
    private static void checkVideoResult(ZaiClient client, String taskId) {
        try {
            VideosResponse response = client.videos().videoGenerationsResult(taskId);
            
            if (response.isSuccess()) {
                VideoObject result = response.getData();
                String status = result.getTaskStatus();
                
                System.out.println("任务状态: " + status);
                
                if ("SUCCESS".equals(status)) {
                    System.out.println("视频生成成功！");
                    if (result.getVideoResult() != null && !result.getVideoResult().isEmpty()) {
                        System.out.println("视频URL: " + result.getVideoResult().get(0).getUrl());
                    }
                } else if ("PROCESSING".equals(status)) {
                    System.out.println("视频仍在生成中，请稍后再次查询...");
                }
            } else {
                System.err.println("查询结果错误: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("查询视频结果时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}