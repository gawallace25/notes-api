package com.gitlab.rurouniwallace.notes.health;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gitlab.rurouniwallace.notes.messages.HealthMessages;

/**
 * Health check object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Health {

	/**
	 * Name of the health checks
	 */
	@JsonProperty("name")
	private String name = "notes-api";
	
	/**
	 * Health checks version
	 */
	@JsonProperty("version")
	private String version = "1";
	
	/**
	 * The check results
	 */
	@JsonProperty("checks")
	private Map<String, HealthCheck> checks = new HashMap<>();
	
	/**
	 * Additional health check properties
	 */
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	/**
	 * Construct a new instance
	 */
	public Health() {
		final HealthCheck heartbeat = new HealthCheck(HealthMessages.STATUS_OK, HealthMessages.HEARTBEAT_ALIVE);
		checks.put("heartbeat", heartbeat);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the checks
	 */
	public Map<String, HealthCheck> getChecks() {
		return checks;
	}

	/**
	 * @param checks the checks to set
	 */
	public void setChecks(Map<String, HealthCheck> checks) {
		this.checks = checks;
	}
	
	/**
	 * Add a new check
	 * 
	 * @param name name of the check to add
	 * @param check value of the check
	 */
	public void addCheck(final String name, final HealthCheck check) {
		checks.put(name, check);
	}

	/**
	 * @return the additionalProperties
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	/**
	 * @param additionalProperties the additionalProperties to set
	 */
	@JsonAnySetter
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	
	
	
}
