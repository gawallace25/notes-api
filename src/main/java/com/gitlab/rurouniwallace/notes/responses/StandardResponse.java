package com.gitlab.rurouniwallace.notes.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Base response model
 *
 */
@JsonInclude(Include.NON_NULL)
public class StandardResponse {

	/**
	 * Response message
	 */
	@JsonProperty
	protected String message;
	
	/**
	 * Response status code
	 */
	@JsonProperty
	protected StatusCode status;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the status
	 */
	public StatusCode getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(StatusCode status) {
		this.status = status;
	}
}
