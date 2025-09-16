package ai.z.openapi.service.fileparsing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 文件解析任务上传响应 DTO
 * 兼容多种响应结构
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileParsingUploadResp {
    /**
     * 任务ID（接口字段 task_id 或 taskId）
     */
    private String taskId;

    /**
     * 返回消息（接口字段 message）
     */
    private String message;

}