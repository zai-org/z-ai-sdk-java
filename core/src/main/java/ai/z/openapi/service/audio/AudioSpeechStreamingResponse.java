package ai.z.openapi.service.audio;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.core.model.FlowableClientResponse;
import ai.z.openapi.service.model.ChatError;
import java.io.File;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.reactivex.rxjava3.core.Flowable;
import lombok.Data;

@Data
public class AudioSpeechStreamingResponse implements FlowableClientResponse<ObjectNode> {

	private int code;

	private String msg;

	private boolean success;

	private ObjectNode data;

	private ChatError error;

	private Flowable<ObjectNode> flowable;

}
