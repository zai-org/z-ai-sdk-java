package ai.z.openapi.service.deserialize;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.model.params.CodeGeexContext;

import java.io.IOException;


public class CodeGeexContextDeserializer extends BaseNodeDeserializer<CodeGeexContext> {

    private static final CodeGeexContextDeserializer instance = new CodeGeexContextDeserializer();

    public CodeGeexContextDeserializer() {
        super(CodeGeexContext.class, null);
    }

    public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
        if (nodeClass == ObjectNode.class) {
            return ObjectDeserializer.getInstance();
        }
        return instance;
    }

    @Override
    public CodeGeexContext getNullValue(DeserializationContext ctxt) {
        return null;
    }

    @Override
    public CodeGeexContext deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentTokenId() == JsonTokenId.ID_START_OBJECT) {
            ObjectNode jsonNodes = deserializeObject(p, ctxt, ctxt.getNodeFactory());
            return new CodeGeexContext(jsonNodes);
        }
        return null;
    }
}
