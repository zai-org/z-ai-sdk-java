package ai.z.openapi.service.fileparsing;

import ai.z.openapi.core.model.ClientRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 文件解析结果下载请求参数
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileParsingDownloadReq implements ClientRequest<FileParsingDownloadReq> {

    /**
     * 解析任务ID（必填）
     */
    private String taskId;

    /**
     * 返回内容格式类型（如 "json", "txt" 等，必填）
     */
    private String formatType;

}