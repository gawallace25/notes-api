package com.gitlab.rurouniwallace.notes.exceptions;

/**
 * An error related to authentication occurred
 */
@SuppressWarnings("serial")
public class AuthenticationException extends Exception {

	/**
	 * Construct a new instance
	 */
	public AuthenticationException() {
		super();
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param message error message
	 */
	public AuthenticationException(final String message) {
		super(message);
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param message error message
	 * @param cause exception that caused this one
	 */
	public AuthenticationException(final String message, final Exception cause) {
		super(message, cause);
	}
}
