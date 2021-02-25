package com.gitlab.rurouniwallace.notes.config;

import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

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
	 * Swagger settings
	 */
	private SwaggerBundleConfiguration swagger;

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

	/**
	 * @return the swagger
	 */
	public SwaggerBundleConfiguration getSwagger() {
		return swagger;
	}

	/**
	 * @param swagger the swagger to set
	 */
	public void setSwagger(SwaggerBundleConfiguration swagger) {
		this.swagger = swagger;
	}
}
