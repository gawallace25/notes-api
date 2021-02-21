package com.gitlab.rurouniwallace.notes.config;

import com.yammer.tenacity.core.config.TenacityConfiguration;

/**
 * Tenacity/Hystrix settings
 *
 */
public class UpstreamTenacityConfiguration {

	/**
	 * Health check Tenacity configurations
	 */
	private TenacityConfiguration health;

	/**
	 * @return the health
	 */
	public TenacityConfiguration getHealth() {
		return health;
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(TenacityConfiguration health) {
		this.health = health;
	}
}
