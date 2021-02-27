package com.gitlab.rurouniwallace.notes.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.gitlab.rurouniwallace.notes.controllers.UserController;
import com.gitlab.rurouniwallace.notes.models.User;
import com.gitlab.rurouniwallace.notes.requests.AuthenticationRequest;
import com.gitlab.rurouniwallace.notes.responses.UserResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * User resource
 */
@Api("/users")
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	
	/**
	 * Logic controller for user operations
	 */
	private final UserController controller;
	
	/**
	 * Construct a new instance
	 * 
	 * @param controller logic controller
	 */
	public UserResource(final UserController controller) {
		this.controller = controller;
	}
	
	/**
	 * Authenticate a user
	 * 
	 * @param authnRequest authentication request
	 * @param response asynchronous API response
	 */
	@POST
	@Path("/authn")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Authenticate a user", response = UserResponse.class)
	public void authenticateUser(final AuthenticationRequest authnRequest, @Suspended final AsyncResponse response) {
		controller.authenticateUser(authnRequest, response);
	}

	/**
	 * Create a new user
	 * 
	 * @param user user to create
	 * @param response created user
	 */
	@POST
	@Timed
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create a new user", response = UserResponse.class)
	public void createUser(final User user, @Suspended final AsyncResponse response) {
		controller.createUser(user, response);
	}
}
