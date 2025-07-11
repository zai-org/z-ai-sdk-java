package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the error body when an ZAI request fails
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZAiError {

    public ZAiErrorDetails error;

    @JsonProperty("contentFilter")
    public List<ContentFilter> contentFilter;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZAiErrorDetails {
        /**
         * Human-readable error message
         */
        String message;

        String type;

        String param;

        /**
         * ZAI error code, for example "invalid_api_key"
         */
        String code;
    }

    /**
     * Sensitive words
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentFilter {
        String level;

        String role;

    }
}
