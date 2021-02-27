package com.gitlab.rurouniwallace.notes.exceptions;

/**
 * An error occurred attempting to access data from a datasource
 */
@SuppressWarnings("serial")
public class DataAccessException extends Exception {

	/**
	 * construct a new instance
	 */
	public DataAccessException() {
		super();
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param message error message
	 */
	public DataAccessException(final String message) {
		super(message);
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param message error message
	 * @param cause exception that caused this one
	 */
	public DataAccessException(final String message, final Exception cause) {
		super(message, cause);
	}
}
