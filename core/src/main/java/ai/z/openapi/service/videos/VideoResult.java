package ai.z.openapi.service.videos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.deserialize.videos.VideoResultDeserializer;
import lombok.Getter;

/**
 * This class represents the result of a video creation process.
 */
@Getter
@JsonDeserialize(using = VideoResultDeserializer.class)
public class VideoResult extends ObjectNode {

    /**
     * Video URL
     */
    @JsonProperty("url")
    private String url;
    
    /**
     * Cover image URL
     */
    @JsonProperty("cover_image_url")
    private String coverImageUrl;

    public VideoResult() {
        super(JsonNodeFactory.instance);
    }

    public VideoResult(ObjectNode objectNode) {
        super(JsonNodeFactory.instance);
        ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();
        if (objectNode.get("url") != null) {
            this.setUrl(objectNode.get("url").asText());
        } else {
            this.setUrl(null);
        }
        if (objectNode.get("cover_image_url") != null) {
            this.setCoverImageUrl(objectNode.get("cover_image_url").asText());
        } else {
            this.setCoverImageUrl(null);
        }
    }

    // Getters and Setters

    public void setUrl(String url) {
        this.url = url;
        this.put("url", url);
    }

    public void setCoverImageUrl(String coverImageUrl){
        this.coverImageUrl = coverImageUrl;
        this.put("cover_image_url",coverImageUrl);
    }
}
