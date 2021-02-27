package com.gitlab.rurouniwallace.notes.exceptions;

/**
 * User authentication was denied
 *
 */
@SuppressWarnings("serial")
public class AuthenticationDeniedException extends AuthenticationException {
	
	/**
	 * Construct a new instance
	 */
	public AuthenticationDeniedException() {
		super();
	}

	/**
	 * Construct a new instance
	 * 
	 * @param message error message
	 */
	public AuthenticationDeniedException(final String message) {
		super(message);
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param message error message
	 * @param cause exception that caused this one
	 */
	public AuthenticationDeniedException(final String message, final Exception cause) {
		super(message, cause);
	}

}
