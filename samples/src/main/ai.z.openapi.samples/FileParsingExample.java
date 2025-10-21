package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.fileparsing.FileParsingDownloadReq;
import ai.z.openapi.service.fileparsing.FileParsingDownloadResponse;
import ai.z.openapi.service.fileparsing.FileParsingResponse;
import ai.z.openapi.service.fileparsing.FileParsingUploadReq;
import ai.z.openapi.utils.StringUtils;

public class FileParsingExample {

    public static void main(String[] args) {
        // 建议通过环境变量设置 API Key
        // export ZAI_API_KEY=your.api_key
        // ZaiClient client = ZaiClient.builder().build();

        // 也可在代码中直接指定 API Key
        ZaiClient client = ZaiClient.builder()
                .apiKey("API Key")
                .build();

        try {
            // 示例1: 创建解析任务
            System.out.println("=== 文件解析任务创建示例 ===");
            String filePath = "your file path";
            String taskId = createFileParsingTaskExample(client, filePath, "pdf", "lite");

            // 示例2: 获取解析结果
            System.out.println("\n=== 获取解析结果示例 ===");
            getFileParsingResultExample(client, taskId);

        } catch (Exception e) {
            System.err.println("发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 示例：创建解析任务（上传文件并解析）
     *
     * @param client ZaiClient 实例
     * @return 解析任务的 taskId
     */
    private static String createFileParsingTaskExample(ZaiClient client, String filePath, String fileType, String toolType) {
        if (StringUtils.isEmpty(filePath)) {
            System.err.println("无效的文件路径。");
            return null;
        }
        try {
            FileParsingUploadReq uploadReq = FileParsingUploadReq.builder()
                    .filePath(filePath)
                    .fileType(fileType)  // 支持: pdf, docx 等
                    .toolType(toolType) // 解析工具类型: lite, prime, expert
                    .build();

            System.out.println("正在上传并创建解析任务...");
            FileParsingResponse response = client.fileParsing().createParseTask(uploadReq);
            if (response.isSuccess()) {
                if (null != response.getData().getTaskId()) {
                    String taskId = response.getData().getTaskId();
                    System.out.println("解析任务创建成功，TaskId: " + taskId);
                    return taskId;
                } else {
                    System.err.println("解析任务创建失败: " + response.getData().getMessage());
                }
            } else {
                System.err.println("解析任务创建失败: " + response.getMsg());
            }
        } catch (Exception e) {
            System.err.println("文件解析任务错误: " + e.getMessage());
        }
        // 返回 null 表示创建失败
        return null;
    }

    /**
     * 示例：获取解析结果
     *
     * @param client ZaiClient 实例
     * @param taskId 解析任务ID
     */
    private static void getFileParsingResultExample(ZaiClient client, String taskId) {
        if (taskId == null || taskId.isEmpty()) {
            System.err.println("无效的任务ID，无法获取解析结果。");
            return;
        }

        try {
            int maxRetry = 100;      // 最多轮询100次
            int intervalMs = 3000;  // 每次间隔3秒
            for (int i = 0; i < maxRetry; i++) {
                FileParsingDownloadReq downloadReq = FileParsingDownloadReq.builder()
                        .taskId(taskId)
                        .formatType("text")
                        .build();

                FileParsingDownloadResponse response = client.fileParsing().getParseResult(downloadReq);

                if (response.isSuccess()) {
                    String status = response.getData().getStatus();
                    System.out.println("当前任务状态: " + status);

                    if ("succeeded".equalsIgnoreCase(status)) {
                        System.out.println("解析结果获取成功！");
                        System.out.println("解析内容: " + response.getData().getContent());
                        System.out.println("内容下载链接: " + response.getData().getParsingResultUrl());
                        return;
                    } else if ("processing".equalsIgnoreCase(status)) {
                        System.out.println("解析进行中，请稍候...");
                        Thread.sleep(intervalMs);
                    } else {
                        System.out.println("解析任务异常，状态: " + status + "，消息: " + response.getData().getMessage());
                        return;
                    }
                } else {
                    System.err.println("解析结果获取失败: " + response.getMsg());
                    return;
                }
            }
            System.out.println("等待超时，请稍后自行查询解析结果。");
        } catch (Exception e) {
            System.err.println("获取解析结果时异常: " + e.getMessage());
        }
    }
}
