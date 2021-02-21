package com.gitlab.rurouniwallace.notes.command;

import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck.Result;
import com.gitlab.rurouniwallace.notes.health.Health;
import com.gitlab.rurouniwallace.notes.health.HealthCheck;
import com.gitlab.rurouniwallace.notes.messages.HealthMessages;
import com.gitlab.rurouniwallace.notes.tenacity.NotesApiDependencyKeys;
import com.yammer.tenacity.core.TenacityCommand;
import com.yammer.tenacity.core.properties.TenacityPropertyKey;

import io.dropwizard.setup.Environment;

/**
 * API health command
 */
public class HealthCommand extends TenacityCommand<Health>{

	/**
	 * Event logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HealthCommand.class);
	
	/**
	 * Dropwizard environment handle
	 */
	private final Environment environment;

	/**
	 * Construct a new instance
	 * 
	 * @param environment dropwizard environment handle
	 */
	public HealthCommand(final Environment environment) {
		super(NotesApiDependencyKeys.HEALTH);
		this.environment = environment;
	}

	/**
	 * Run the command
	 * 
	 * @return health checks
	 */
	@Override
	protected Health run() throws Exception {
		LOGGER.info("Running health check");
		
		final SortedMap<String, Result> results = environment.healthChecks().runHealthChecks(environment.getHealthCheckExecutorService());
		final Health health = new Health();
		
		
		for (final String key : results.keySet()) {
			final HealthCheck okCheck = new HealthCheck(HealthMessages.STATUS_OK, key + " is healthy");
			
			final HealthCheck criticalCheck = new HealthCheck(HealthMessages.STATUS_CRITICAL, key + " has a problem");
			
			final Result result = results.get(key);
			
			final HealthCheck check = result.isHealthy() ? okCheck : criticalCheck;
		
			health.addCheck(key, check);
		}
		
		return health;
	}

}
