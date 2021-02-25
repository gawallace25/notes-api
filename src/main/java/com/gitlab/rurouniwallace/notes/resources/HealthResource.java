package com.gitlab.rurouniwallace.notes.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.gitlab.rurouniwallace.notes.command.CommandRunner;
import com.gitlab.rurouniwallace.notes.command.HealthCommand;
import com.gitlab.rurouniwallace.notes.health.Health;
import io.dropwizard.setup.Environment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * API Health REST resource
 *
 */
@Api("/health")
@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

	/**
	 * Event logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HealthResource.class);

	/**
	 * Dropwizard runtime environment/bootstrapping
	 */
	private final Environment environment;
	
	/**
	 * Construct a new instance
	 * 
	 * @param environment Dropwizard runtime environment/bootstrapping
	 */
	public HealthResource(final Environment environment) {
		this.environment = environment;
	}
	
	/**
	 * Check API health
	 * 
	 * @param response asynchronous API response
	 */
	@GET
	@Timed
	@ApiOperation(value = "Perform a health check on the service", response = Health.class)
	public void checkHealth(@Suspended final AsyncResponse response) {
		CommandRunner.<Health>run(response, new HealthCommand(environment), LOGGER);
	}
}
