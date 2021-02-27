package com.gitlab.rurouniwallace.notes.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User authentication request
 */
public class AuthenticationRequest {

	/**
	 * User email address
	 */
	@JsonProperty
	private String email;
	
	/**
	 * User password
	 */
	@JsonProperty
	private String password;

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
