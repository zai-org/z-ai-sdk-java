package ai.z.openapi.service.layoutparsing;

import ai.z.openapi.service.model.Usage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayoutParsingResult {

	private String id;

	private Long created;

	private String model;

	@JsonProperty("md_results")
	private String mdResults;

	@JsonProperty("layout_details")
	private List<LayoutDetail> layoutDetails;

	private String visualizer;

	@JsonProperty("data_info")
	private DataInfo dataInfo;

	private Usage usage;

	@JsonProperty("request_id")
	private String requestId;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonDeserialize(using = LayoutDetailDeserializer.class)
	public static class LayoutDetail {

		private Integer index;

		private String label;

		@JsonProperty("bbox_2d")
		private Object bbox2dRaw;

		private List<Integer> bbox2d;

		private String content;

		private Integer height;

		private Integer width;

		@JsonProperty("bbox_2d")
		public void setBbox2dRaw(Object bbox2dRaw) {
			this.bbox2dRaw = bbox2dRaw;
			this.bbox2d = convertBbox2d(bbox2dRaw);
		}

		private List<Integer> convertBbox2d(Object bbox2dRaw) {
			List<Integer> result = new ArrayList<>();
			if (bbox2dRaw == null) {
				return result;
			}

			try {
				if (bbox2dRaw instanceof List) {
					List<?> rawList = (List<?>) bbox2dRaw;
					if (!rawList.isEmpty() && rawList.get(0) instanceof List) {
						// Handle nested array: [[x1, y1, x2, y2]]
						List<?> innerList = (List<?>) rawList.get(0);
						for (Object item : innerList) {
							if (item instanceof Number) {
								result.add(((Number) item).intValue());
							}
						}
					}
					else {
						// Handle flat array: [x1, y1, x2, y2]
						for (Object item : rawList) {
							if (item instanceof Number) {
								result.add(((Number) item).intValue());
							}
						}
					}
				}
			}
			catch (Exception e) {
				// Log error if needed, but return empty list to avoid breaking the
				// parsing
			}

			return result;
		}

	}

	static class LayoutDetailDeserializer extends JsonDeserializer<LayoutDetail> {

		@Override
		public LayoutDetail deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			JsonNode node = p.getCodec().readTree(p);
			if (node == null || node.isNull()) {
				return null;
			}

			LayoutDetail detail = new LayoutDetail();
			if (node.isArray()) {
				// Expected order (best-effort): [index, label, bbox_2d, content, height,
				// width]
				setIfPresentInt(detail, node, 0, "index");
				setIfPresentText(detail, node, 1, "label");
				setIfPresentBbox(detail, node, 2);
				setIfPresentText(detail, node, 3, "content");
				setIfPresentInt(detail, node, 4, "height");
				setIfPresentInt(detail, node, 5, "width");
				return detail;
			}

			if (node.isObject()) {
				JsonNode indexNode = node.get("index");
				if (indexNode != null && indexNode.isNumber()) {
					detail.setIndex(indexNode.intValue());
				}

				JsonNode labelNode = node.get("label");
				if (labelNode != null && labelNode.isTextual()) {
					detail.setLabel(labelNode.asText());
				}

				JsonNode bboxNode = node.get("bbox_2d");
				if (bboxNode != null && !bboxNode.isNull()) {
					detail.setBbox2dRaw(toJavaList(bboxNode));
				}

				JsonNode contentNode = node.get("content");
				if (contentNode != null && contentNode.isTextual()) {
					detail.setContent(contentNode.asText());
				}

				JsonNode heightNode = node.get("height");
				if (heightNode != null && heightNode.isNumber()) {
					detail.setHeight(heightNode.intValue());
				}

				JsonNode widthNode = node.get("width");
				if (widthNode != null && widthNode.isNumber()) {
					detail.setWidth(widthNode.intValue());
				}
				return detail;
			}

			return detail;
		}

		private static void setIfPresentInt(LayoutDetail detail, JsonNode array, int idx, String field) {
			JsonNode n = array.size() > idx ? array.get(idx) : null;
			if (n == null || !n.isNumber()) {
				return;
			}
			int v = n.intValue();
			if ("index".equals(field)) {
				detail.setIndex(v);
			}
			else if ("height".equals(field)) {
				detail.setHeight(v);
			}
			else if ("width".equals(field)) {
				detail.setWidth(v);
			}
		}

		private static void setIfPresentText(LayoutDetail detail, JsonNode array, int idx, String field) {
			JsonNode n = array.size() > idx ? array.get(idx) : null;
			if (n == null || !n.isTextual()) {
				return;
			}
			String v = n.asText();
			if ("label".equals(field)) {
				detail.setLabel(v);
			}
			else if ("content".equals(field)) {
				detail.setContent(v);
			}
		}

		private static void setIfPresentBbox(LayoutDetail detail, JsonNode array, int idx) {
			JsonNode n = array.size() > idx ? array.get(idx) : null;
			if (n == null || n.isNull()) {
				return;
			}
			detail.setBbox2dRaw(toJavaList(n));
		}

		private static Object toJavaList(JsonNode node) {
			if (node == null || node.isNull()) {
				return null;
			}
			if (!node.isArray()) {
				return null;
			}

			List<Object> out = new ArrayList<>();
			for (JsonNode child : node) {
				if (child == null || child.isNull()) {
					out.add(null);
				}
				else if (child.isArray()) {
					out.add(toJavaList(child));
				}
				else if (child.isNumber()) {
					out.add(child.intValue());
				}
				else if (child.isTextual()) {
					out.add(child.asText());
				}
				else {
					out.add(child.toString());
				}
			}
			return out;
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DataInfo {

		@JsonProperty("num_pages")
		private Integer numPages;

		private List<PageInfo> pages;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PageInfo {

		private Integer width;

		private Integer height;

	}

}
