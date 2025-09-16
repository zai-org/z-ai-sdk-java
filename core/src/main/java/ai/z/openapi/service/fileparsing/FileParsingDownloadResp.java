package ai.z.openapi.service.fileparsing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 文件解析结果响应 DTO
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileParsingDownloadResp {

    /**
     * 解析任务ID
     */
    @JsonProperty("task_id")
    private String taskId;

    /**
     * 结果状态（如 succeeded、failed 等）
     */
    @JsonProperty("status")
    private String status;

    /**
     * 响应消息
     */
    @JsonProperty("message")
    private String message;

    /**
     * 解析结果内容
     */
    @JsonProperty("content")
    private String content;

    /**
     * 解析结果下载链接（如有）
     */
    @JsonProperty("parsing_result_url")
    private String parsingResultUrl;


}