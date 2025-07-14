package ai.z.openapi.service.web_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSearchResp {

	private String refer;

	private String title;

	private String link;

	private String media;

	private String content;

	private String icon;

	@JsonProperty("publish_date")
	private String publishDate;

}
