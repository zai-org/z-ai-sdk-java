package ai.z.openapi.samples;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.fileparsing.FileParsingDownloadResponse;
import ai.z.openapi.service.fileparsing.FileParsingUploadReq;
import ai.z.openapi.utils.StringUtils;

public class FileParsingSyncExample {

    public static void main(String[] args) {
        // 建议通过环境变量设置 API Key
        // export ZAI_API_KEY=your.api_key
        // ZaiClient client = ZaiClient.builder().build();

        // 也可在代码中直接指定 API Key
        ZaiClient client = ZaiClient.builder()
                .apiKey("API Key")
                .build();

        try {
            System.out.println("=== 文件解析任务创建示例 ===");

            String filePath = "your file path";
            FileParsingDownloadResponse result = syncFileParsingTaskExample(client, filePath, "pdf", "prime-sync");

            System.out.println("解析任务创建成功，TaskId:" + result.getData().getTaskId());
            System.out.println("文件内容" + result.getData().getContent());
            System.out.println("下载链接" + result.getData().getParsingResultUrl());

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
    private static FileParsingDownloadResponse syncFileParsingTaskExample(ZaiClient client, String filePath, String fileType, String toolType) {
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
            return client.fileParsing().syncParse(uploadReq);
        } catch (Exception e) {
            System.err.println("文件解析任务错误: " + e.getMessage());
        }
        // 返回 null 表示创建失败
        return null;
    }


}
