package ai.z.openapi.service.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordsResult {

	private Location location; // Location information for detected text

	private String words; // Recognized text

}