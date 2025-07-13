package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that accumulates chat messages and provides utility methods for handling message
 * chunks and function calls within a chat stream. This class is immutable.
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageAccumulator {

	private ChatMessage accumulatedMessage;

	private Delta delta;

	private Choice choice;

	private Usage usage;

	private Long created;

	private String id;

}
