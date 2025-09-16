package ai.z.openapi.service.fileparsing;

import ai.z.openapi.core.model.ClientRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 文件解析任务上传请求参数
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileParsingUploadReq implements ClientRequest<FileParsingUploadReq> {

    /**
     * 本地文件路径
     */
    private String filePath;

    /**
     * 工具类型，例如 "pdf_parser"
     */
    private String toolType;

    /**
     * 文件类型，例如 "pdf", "doc", etc.
     */
    private String fileType;

}