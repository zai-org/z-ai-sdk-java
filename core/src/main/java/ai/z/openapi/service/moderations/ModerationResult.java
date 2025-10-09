package ai.z.openapi.service.moderations;

import ai.z.openapi.service.model.Usage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result data from the moderation API containing safety analysis results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationResult {

    /**
     * Unique request identifier for tracking.
     */
    private String requestId;

    /**
     * Processing time in milliseconds.
     */
    private Long processedTime;

    /**
     * List of moderation results for each input item.
     */
    private List<ModerationItem> resultList;

    /**
     * Token usage information for the request.
     */
    private Usage usage;

    /**
     * Individual moderation result for a single input item.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModerationItem {

        /**
         * Type of content being moderated (text, image, video, audio).
         */
        private String contentType;

        /**
         * Risk level assessment: "low", "medium", "high".
         */
        private String riskLevel;

        /**
         * Specific type of risk detected (e.g., "violence", "sexual", "hate").
         */
        private String riskType;

        /**
         * Confidence score for the moderation result (0.0 to 1.0).
         */
        private Double confidence;

        /**
         * Additional details about the moderation result.
         */
        private String details;

        /**
         * Check if the content is flagged as unsafe.
         * @return true if risk level is medium or high
         */
        public boolean isFlagged() {
            return "medium".equalsIgnoreCase(riskLevel) || "high".equalsIgnoreCase(riskLevel);
        }

        /**
         * Check if the content is safe.
         * @return true if risk level is low
         */
        public boolean isSafe() {
            return "low".equalsIgnoreCase(riskLevel);
        }
    }
}