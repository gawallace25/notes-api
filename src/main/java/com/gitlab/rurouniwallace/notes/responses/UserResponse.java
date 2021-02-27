package com.gitlab.rurouniwallace.notes.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gitlab.rurouniwallace.notes.models.User;

/**
 * Response to a user request
 */
@JsonInclude(Include.NON_NULL)
public class UserResponse extends StandardResponse {
	
	/**
	 * Construct a new instance
	 */
	public UserResponse() {
		// empty constructor
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param message the response message
	 * @param status the response status
	 */
	public UserResponse(final String message, final StatusCode status) {
		this.message = message;
		this.status = status;
	}

	/**
	 * Construct a new instance
	 * 
	 * @param user the user for the response
	 * @param status the response status
	 */
	public UserResponse(final User user, final StatusCode status) {
		this.user = user;
		this.status = status;
	}
	
	/**
	 * User to provide in response
	 */
	@JsonProperty
	private User user;

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
