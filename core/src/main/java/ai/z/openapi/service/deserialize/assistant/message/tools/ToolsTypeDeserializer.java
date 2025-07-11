package ai.z.openapi.service.deserialize.assistant.message.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import ai.z.openapi.service.assistant.message.tools.ToolsType;
import ai.z.openapi.service.deserialize.JsonTypeField;
import ai.z.openapi.service.deserialize.JsonTypeMapping;

import java.io.IOException;
import java.lang.reflect.Field;

public class ToolsTypeDeserializer extends JsonDeserializer<ToolsType> {

	@Override
	public ToolsType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.readValueAsTree();

		// Get JsonTypeMapping annotation from MessageContent class
		JsonTypeMapping mapping = ToolsType.class.getAnnotation(JsonTypeMapping.class);
		if (mapping == null) {
			throw new IllegalStateException("Missing JsonTypeMapping annotation on MessageContent class");
		}

		// Iterate through classes defined in annotation to determine suitable class based
		// on annotation or static method values
		for (Class<?> clazz : mapping.value()) {
			JsonTypeField typeField = clazz.getAnnotation(JsonTypeField.class);

			if (typeField != null && node.has(typeField.value())) {
				try {
					// Create instance of the class
					Object obj = clazz.getDeclaredConstructor().newInstance();

					// Use reflection to manually set field values
					for (Field field : clazz.getDeclaredFields()) {
						field.setAccessible(true);
						// field.getName() gets the value from JsonProperty annotation
						// above
						JsonProperty annotation = field.getAnnotation(JsonProperty.class);
						String name = null;
						if (annotation == null) {
							name = field.getName();
						}
						else {
							name = annotation.value();
						}
						if (node.has(name)) {
							// Set values based on field type, assuming fields are
							// primitive types or strings
							if (field.getType().equals(String.class)) {
								field.set(obj, node.get(name).asText());
							}
							else if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
								field.set(obj, node.get(name).asInt());
							}
							else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
								field.set(obj, node.get(name).asBoolean());

							}
							else {
								// For other types, use ObjectMapper for direct conversion
								Object o = new ObjectMapper().treeToValue(node.get(name), field.getType());
								field.set(obj, o);
							}
						}
					}

					return (ToolsType) obj;
				}
				catch (Exception e) {
					throw new RuntimeException("Error while creating instance of " + clazz.getName(), e);
				}
			}
		}

		throw new IllegalArgumentException("Cannot determine type for JSON: " + node.toString());
	}

}
