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
	 * Security settings
	 */
	private SecurityConfiguration security;
	
	/**
	 * SQL configurations
	 */
	private SqlFactory sql;

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

	/**
	 * @return the security
	 */
	public SecurityConfiguration getSecurity() {
		return security;
	}

	/**
	 * @param security the security to set
	 */
	public void setSecurity(SecurityConfiguration security) {
		this.security = security;
	}

	/**
	 * @return the sql
	 */
	public SqlFactory getSql() {
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(SqlFactory sql) {
		this.sql = sql;
	}
}
