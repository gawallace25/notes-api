package com.gitlab.rurouniwallace.notes.exceptions;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends DataAccessException {

	public EntityAlreadyExistsException() {
		super();
	}
	
	public EntityAlreadyExistsException(final String message) {
		super(message);
	}
	
	public EntityAlreadyExistsException(final String message, final Exception cause) {
		super(message, cause);
	}
}
