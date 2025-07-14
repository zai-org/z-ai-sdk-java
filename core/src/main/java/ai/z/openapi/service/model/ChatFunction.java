package ai.z.openapi.service.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatFunction {

	private String name;

	private String description;

	private ChatFunctionParameters parameters;

	private List<String> required;

}
