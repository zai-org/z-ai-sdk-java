package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Usage statistics for API calls.
 * This class contains token and call count information for tracking API usage.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usage {
    /**
     * Number of tokens in the prompt/input.
     */
    @JsonProperty("prompt_tokens")
    private int promptTokens;
    
    /**
     * Number of tokens in the completion/output.
     */
    @JsonProperty("completion_tokens")
    private int completionTokens;
    
    /**
     * Total number of tokens used (prompt + completion).
     */
    @JsonProperty("total_tokens")
    private int totalTokens;

    /**
     * Total number of API calls made.
     */
    @JsonProperty("total_calls")
    private int totalCalls;

    public int getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(int promptTokens) {
        this.promptTokens = promptTokens;
    }

    public int getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(int completionTokens) {
        this.completionTokens = completionTokens;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

}