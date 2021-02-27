package com.gitlab.rurouniwallace.notes.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SqlFactory {

	/**
	 * The SQL connection URL
	 */
	@JsonProperty
	private String connectionUrl;

	/**
	 * @return the connectionUrl
	 */
	public String getConnectionUrl() {
		return connectionUrl;
	}

	/**
	 * @param connectionUrl the connectionUrl to set
	 */
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	/**
	 * Build a datasource to create SQL database connections
	 * 
	 * @return the SQL datasource
	 */
	public DataSource buildDataSource() {
		final ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectionUrl);
		
		final PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
	
		final ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<PoolableConnection>(poolableConnectionFactory);
	
		return new PoolingDataSource<PoolableConnection>(connectionPool);
	}
}
