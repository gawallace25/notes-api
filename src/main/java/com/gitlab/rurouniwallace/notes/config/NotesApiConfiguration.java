package com.gitlab.rurouniwallace.notes.config;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;
import javax.validation.constraints.*;

/**
 * Application-level configurations
 */
public class NotesApiConfiguration extends Configuration {
    
	/**
	 * Application Hystrix/Tenacity settings
	 */
	private UpstreamTenacityConfiguration upstreamTenacity;

	/**
	 * @return the upstreamTenacity
	 */
	public UpstreamTenacityConfiguration getUpstreamTenacity() {
		return upstreamTenacity;
	}

	/**
	 * @param upstreamTenacity the upstreamTenacity to set
	 */
	public void setUpstreamTenacity(UpstreamTenacityConfiguration upstreamTenacity) {
		this.upstreamTenacity = upstreamTenacity;
	}
}
