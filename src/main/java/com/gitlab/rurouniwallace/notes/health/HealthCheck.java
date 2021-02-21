package com.gitlab.rurouniwallace.notes.health;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gitlab.rurouniwallace.notes.messages.HealthMessages;

/**
 * Individual health check
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"status", "message"})
public class HealthCheck {

	@JsonProperty("status")
	private String status = HealthMessages.STATUS_OK;
	
	@JsonProperty("message")
	private String message = HealthMessages.STATUS_OK;
	
	@JsonIgnore
	private final Map<String, Object> additionalProperties;
	
	public HealthCheck() {
		this.additionalProperties = new HashMap<>();
	}
	
	public HealthCheck(final String status, final String message) {
		super();
		this.setStatus(status);
		this.setMessage(message);
		this.additionalProperties = new HashMap<>();
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

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
	 * @return the additionalProperties
	 */
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	
	
}
