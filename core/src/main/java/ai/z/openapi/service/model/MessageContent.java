package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class MessageContent {

	/** 消息类型 text image_url video_url */
	private String type;

	/** 消息内容 when type is text */
	private String text;

	/** 消息图片URL when type is image_url */
	@JsonProperty("image_url")
	private ImageUrl imageUrl;

	/** 消息视频URL when type is video_url */
	@JsonProperty("video_url")
	private VideoUrl videoUrl;
}
