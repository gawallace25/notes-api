package com.gitlab.rurouniwallace.notes.config;

/**
 * Security configuration data
 *
 */
public class SecurityConfiguration {
	
	/**
	 * Bcrypt hash cost factor. The higher the cost, the slower and more secure the algorithm
	 */
	private Integer hashCost;
	
	/**
	 * Construct a new instance
	 */
	public SecurityConfiguration() {
		// empty constructor
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param hashCost Bcrypt hash cost factor
	 */
	public SecurityConfiguration(final Integer hashCost) {
		this.hashCost = hashCost;
	}

	/**
	 * @return the hashCost
	 */
	public Integer getHashCost() {
		return hashCost;
	}

	/**
	 * @param hashCost the hashCost to set
	 */
	public void setHashCost(Integer hashCost) {
		this.hashCost = hashCost;
	}
}
