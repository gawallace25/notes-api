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

public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	private final IAccessesUsers userDao;
	
	public UserController(final IAccessesUsers userDao) {
		this.userDao = userDao;
	}
	
	public void authenticateUser(final AuthenticationRequest authnRequest, final AsyncResponse response) {
		if (authnRequest.getEmail() == null || authnRequest.getEmail().isBlank()) {
			response.resume(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY_422, StatusCode.INVALID_ARGUMENTS, "Email address required"));
		}
		
		if (authnRequest.getPassword() == null || authnRequest.getPassword().isBlank()) {
			response.resume(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY_422, StatusCode.INVALID_ARGUMENTS, "Password required"));
		}
		
		final AuthenticateUserCommand command = new AuthenticateUserCommand(userDao, authnRequest.getEmail(), authnRequest.getPassword());
	
		CommandRunner.run(response, command, LOGGER);
	}

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
	
	private Response buildErrorResponse(final int httpStatus, final StatusCode statusCode, final String message) {
		final UserResponse userResponse = new UserResponse(message, statusCode);
		
		return Response.status(httpStatus).entity(userResponse).build();
	}
}
