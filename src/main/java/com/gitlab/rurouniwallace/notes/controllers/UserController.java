package com.gitlab.rurouniwallace.notes.controllers;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitlab.rurouniwallace.notes.command.AuthenticateUserCommand;
import com.gitlab.rurouniwallace.notes.command.CommandRunner;
import com.gitlab.rurouniwallace.notes.command.CreateUserCommand;
import com.gitlab.rurouniwallace.notes.dao.IAccessesUsers;
import com.gitlab.rurouniwallace.notes.models.User;
import com.gitlab.rurouniwallace.notes.requests.AuthenticationRequest;
import com.gitlab.rurouniwallace.notes.responses.StatusCode;
import com.gitlab.rurouniwallace.notes.responses.UserResponse;

/**
 * User resource logic controller
 */
public class UserController {
	
	/**
	 * Event logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	/**
	 * User data access interface
	 */
	private final IAccessesUsers userDao;
	
	/**
	 * Construct a new instance
	 * 
	 * @param userDao the user data access interface
	 */
	public UserController(final IAccessesUsers userDao) {
		this.userDao = userDao;
	}
	
	/**
	 * Authenticate a user
	 * 
	 * @param authnRequest the authentication request
	 * @param response the authentication response
	 */
	public void authenticateUser(final AuthenticationRequest authnRequest, final AsyncResponse response) {
		if (authnRequest.getEmail() == null || authnRequest.getEmail().isEmpty()) {
			response.resume(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY_422, StatusCode.INVALID_ARGUMENTS, "Email address required"));
		}
		
		if (authnRequest.getPassword() == null || authnRequest.getPassword().isEmpty()) {
			response.resume(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY_422, StatusCode.INVALID_ARGUMENTS, "Password required"));
		}
		
		final AuthenticateUserCommand command = new AuthenticateUserCommand(userDao, authnRequest.getEmail(), authnRequest.getPassword());
	
		CommandRunner.run(response, command, LOGGER);
	}

	/**
	 * Create a new user
	 * 
	 * @param user the user to create
	 * @param response the response
	 */
	public void createUser(final User user, final AsyncResponse response) {
		if (user.getUuid() != null) {
			response.resume(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY_422, StatusCode.INVALID_ARGUMENTS, "UUID may not be asserted when creating a user"));
		}
		
		if (user.getEmail() == null || user.getEmail().isBlank()) {
			response.resume(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY_422, StatusCode.INVALID_ARGUMENTS, "Email address required"));
		}
		
		final CreateUserCommand command = new CreateUserCommand(userDao, user);
		
		CommandRunner.run(response,command, LOGGER);
	}
	
	/**
	 * Build an error HTTP response
	 * 
	 * @param httpStatus HTTP status
	 * @param statusCode response status code
	 * @param message error message
	 * @return the HTTP response
	 */
	private Response buildErrorResponse(final int httpStatus, final StatusCode statusCode, final String message) {
		final UserResponse userResponse = new UserResponse(message, statusCode);
		
		return Response.status(httpStatus).entity(userResponse).build();
	}
}
