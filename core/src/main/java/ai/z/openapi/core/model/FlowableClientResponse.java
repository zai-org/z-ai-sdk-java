package ai.z.openapi.core.model;

import io.reactivex.Flowable;

/**
 * Client response interface with reactive stream support.
 * Extends ClientResponse to provide Flowable stream functionality.
 * 
 * @param <T> response data type
 */
public interface FlowableClientResponse<T> extends ClientResponse<T> {

    /**
     * Sets the reactive stream for this response.
     * 
     * @param stream Flowable stream containing response data
     */
    void setFlowable(Flowable<T> stream);
}
