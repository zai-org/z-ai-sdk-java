package ai.z.openapi.service.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.Map;

/**
 * Configuration class for web search functionality. This class defines various parameters
 * and settings for web search operations.
 */
@Getter
public class WebSearch extends ObjectNode {

	/**
	 * Whether to enable search, defaults to enabled. true: enabled false: disabled
	 */
	private Boolean enable;

	/**
	 * Whether to return search results, defaults to returning results. true: return
	 * results false: do not return results
	 */
	private Boolean search_result;

	/**
	 * Force search for custom keyword content. The model will use the results from custom
	 * search keywords as background knowledge to answer user conversations.
	 */
	private String search_query;

	/**
	 * Prompt for specifying search engine output results.
	 */
	private String search_prompt;

	/**
	 * Specify the search engine to use.
	 */
	private String search_engine;

	/**
	 * Whether search results must be obtained before answering.
	 */
	private Boolean require_search;

	/**
	 * Output position of search results.
	 */
	private String result_sequence;

	/**
	 * Number of results to return.
	 */
	private Integer count;

	/**
	 * Domain filter to limit search result scope.
	 */
	private String search_domain_filter;

	/**
	 * Recency filter to limit search result scope.
	 */
	private String search_recency_filter;

	/**
	 * Control the word count of web page summaries.
	 */
	private String content_size;

	public WebSearch() {
		super(JsonNodeFactory.instance);
	}

	public WebSearch(JsonNodeFactory nc, Map<String, JsonNode> kids) {
		super(nc, kids);
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
		this.put("enable", enable);
	}

	public void setSearch_result(Boolean search_result) {
		this.search_result = search_result;
		this.put("search_result", search_result);
	}

	public void setSearch_query(String search_query) {
		this.search_query = search_query;
		this.put("search_query", search_query);
	}

	public void setSearch_prompt(String search_prompt) {
		this.search_prompt = search_prompt;
		this.put("search_prompt", search_prompt);
	}

	public void setSearch_engine(String search_engine) {
		this.search_engine = search_engine;
		this.put("search_engine", search_engine);
	}

	public void setRequire_search(Boolean require_search) {
		this.require_search = require_search;
		this.put("require_search", require_search);
	}

	public void setResult_sequence(String result_sequence) {
		this.result_sequence = result_sequence;
		this.put("result_sequence", result_sequence);
	}

	public void setCount(Integer count) {
		this.count = count;
		this.put("count", count);
	}

	public void setSearch_domain_filter(String search_domain_filter) {
		this.search_domain_filter = search_domain_filter;
		this.put("search_domain_filter", search_domain_filter);
	}

	public void setSearch_recency_filter(String search_recency_filter) {
		this.search_recency_filter = search_recency_filter;
		this.put("search_recency_filter", search_recency_filter);
	}

	public void setContent_size(String content_size) {
		this.content_size = content_size;
		this.put("content_size", content_size);
	}

}
