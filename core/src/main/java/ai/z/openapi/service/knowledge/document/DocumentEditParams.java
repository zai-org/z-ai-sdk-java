package ai.z.openapi.service.knowledge.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

/**
 * This class represents the parameters for editing a document in the knowledge base.
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentEditParams implements ClientRequest<DocumentEditParams> {


    /**
     * Knowledge ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * Knowledge type:
     * <ul>
     *   <li>1: Article knowledge: supports pdf, url, docx</li>
     *   <li>2: Q&A knowledge-document: supports pdf, url, docx</li>
     *   <li>3: Q&A knowledge-table: supports xlsx</li>
     *   <li>4: Product library-table: supports xlsx</li>
     *   <li>5: Custom: supports pdf, url, docx</li>
     * </ul>
     */
    @JsonProperty("knowledge_type")
    private Integer knowledgeType;

    /**
     * Chunk rules when knowledge type is custom (knowledge_type=5), default \n
     */
    @JsonProperty("custom_separator")
    private List<String> customSeparator;

    /**
     * Chunk word count when knowledge type is custom (knowledge_type=5), range: 20-2000, default 300
     */
    @JsonProperty("sentence_size")
    private Integer sentenceSize;

    /**
     * Callback URL
     */
    @JsonProperty("callback_url")
    private String callbackUrl;

    /**
     * Headers to carry during callback
     */
    @JsonProperty("callback_header")
    private Map<String, String> callbackHeader;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getKnowledgeType() {
        return knowledgeType;
    }

    public void setKnowledgeType(Integer knowledgeType) {
        this.knowledgeType = knowledgeType;
    }

    public List<String> getCustomSeparator() {
        return customSeparator;
    }

    public void setCustomSeparator(List<String> customSeparator) {
        this.customSeparator = customSeparator;
    }

    public Integer getSentenceSize() {
        return sentenceSize;
    }

    public void setSentenceSize(Integer sentenceSize) {
        this.sentenceSize = sentenceSize;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public Map<String, String> getCallbackHeader() {
        return callbackHeader;
    }

    public void setCallbackHeader(Map<String, String> callbackHeader) {
        this.callbackHeader = callbackHeader;
    }

}
