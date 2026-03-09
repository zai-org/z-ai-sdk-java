package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocReference implements Serializable {

    /** 文档索引 */
    private Integer index;

    /** 文档类型 */
    private String doc_type;

    /** 文档名称 */
    private String doc_name;

}