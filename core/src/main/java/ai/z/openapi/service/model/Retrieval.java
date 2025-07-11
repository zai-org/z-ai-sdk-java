package ai.z.openapi.service.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Getter
public class Retrieval extends ObjectNode {


    /**
     * When involving knowledge base ID, please go to the knowledge base module of the open platform to create or obtain it.
     */
    private String knowledge_id;


    /**
     * Knowledge base template when requesting the model, default template:
     * Find the answer to the question
     * """
     * {{question}}
     * """
     * from the document
     * """
     * {{ knowledge}}
     * """
     * If an answer is found, use only document statements to answer the question. If no answer is found, use your own knowledge to answer and tell the user that the information is not from the document.
     * Don't repeat the question, start answering directly
     *
     * Note: When users customize templates, the knowledge base content placeholder and user-side question placeholder must be {{ knowledge}} and {{question}}, other template content can be defined by users according to actual scenarios
     */
    private String prompt_template;

    public Retrieval(){
        super(JsonNodeFactory.instance);
    }
    public Retrieval(JsonNodeFactory nc, Map<String, JsonNode> kids) {
        super(nc, kids);
    }

    public void setKnowledge_id(String knowledge_id) {
        this.knowledge_id = knowledge_id;
        this.put("knowledge_id",knowledge_id);
    }

    public void setPrompt_template(String prompt_template) {
        this.prompt_template = prompt_template;
        this.put("prompt_template",prompt_template);
    }
}
