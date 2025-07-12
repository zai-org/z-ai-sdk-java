package ai.z.openapi.service.model;

import ai.z.openapi.core.model.FlowableClientResponse;
import io.reactivex.Flowable;
import lombok.Data;

@Data
public class ChatCompletionResponse implements FlowableClientResponse<ModelData> {

	private int code;

	private String msg;

	private boolean success;

	private ModelData data;

	private Flowable<ModelData> flowable;

	private ChatError error;

	public ChatCompletionResponse() {
	}

	public ChatCompletionResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
