package com.gitlab.rurouniwallace.notes.dao;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;

import com.gitlab.rurouniwallace.notes.config.SecurityConfiguration;
import com.gitlab.rurouniwallace.notes.exceptions.AuthenticationDeniedException;
import com.gitlab.rurouniwallace.notes.exceptions.AuthenticationException;
import com.gitlab.rurouniwallace.notes.exceptions.DataAccessException;
import com.gitlab.rurouniwallace.notes.exceptions.EntityAlreadyExistsException;
import com.gitlab.rurouniwallace.notes.models.User;

import org.bouncycastle.crypto.generators.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data access layer to communicate with an SQL database
 */
public class SqlDao implements IAccessesUsers {
	
	/**
	 * Query for retrieving users from the database by email
	 */
	private static final String LOOKUP_USER_BY_EMAIL_STATEMENT = "SELECT * FROM Users WHERE email = ?";
	
	/**
	 * Query for inserting users into the database
	 */
	private static final String INSERT_USER_STATEMENT = "INSERT INTO Users(uuid, email, password, phone, givenName, surname) VALUES (?, ?, ?, ?, ?, ?)";
	
	/**
	 * Event logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlDao.class);
	
	/**
	 * Security configurations
	 */
	private final SecurityConfiguration securityConfiguration;
	
	/**
	 * SQL connection pool
	 */
	private final DataSource datasource;
	
	/**
	 * Construct a new instance
	 * 
	 * @param datasource SQL connection source
	 * @param securityConfiguration application security settings
	 */
	public SqlDao(final DataSource datasource, final SecurityConfiguration securityConfiguration) {
		this.datasource = datasource;
		this.securityConfiguration = securityConfiguration;
	}

	/**
	 * Authenticate a user with their email and password
	 * 
	 * @param email user email address
	 * @param password user password
	 * @return the user data
	 * @throws DataAccessException 
	 * @throws AuthenticationException 
	 */
	@Override
	public User authenticateUser(final String email, final String password) throws DataAccessException, AuthenticationException {
		final Connection connection = buildConnection();
		
		final ResultSet resultSet = runStatement(connection, LOOKUP_USER_BY_EMAIL_STATEMENT, Arrays.asList(email), true);
		
		final List<User> users = readUsersFromResultSet(resultSet);
		
		if (users.isEmpty()) {
			DbUtils.closeQuietly(connection);
			throw new AuthenticationDeniedException("User not found");
		}
		
		final User userFromDatabase = users.get(0);
		
		final String hashedPassword = userFromDatabase.getPassword();
		if (!checkPassword(password, hashedPassword)) {
			DbUtils.closeQuietly(connection);
			throw new AuthenticationDeniedException("Passwords don't match");
		}
		
		DbUtils.closeQuietly(connection);
		
		return userFromDatabase;
	}
	
	/**
	 * Submit a user to the database to be registered
	 * 
	 * @param user the user to register
	 * @return the registered user payload
	 * @throws DataAccessException if adding the user to the database fails
	 */
	@Override
	public User registerUser(final User user) throws DataAccessException {
		
		final Connection connection = buildConnection();
		
		final UUID uuid = UUID.randomUUID();
		
		try {
			runStatement(connection, INSERT_USER_STATEMENT, Arrays.asList(uuid, user.getEmail(), hashPassword(user.getPassword()), user.getPhone(), user.getGivenName(), user.getSurname()), false, new IHandlesSqlErrors() {

				/**
				 * Check for the SQL state to see if  a uniqueness constraint was violated
				 * 
				 * @param e the exception to handle
				 */
				@Override
				public void handleError(final SQLException e) throws DataAccessException {
					if (e.getSQLState().equals("23505")) {
						throw new EntityAlreadyExistsException("A user with the provided email already exists", e);
					}

				}
			});
		}catch (final GeneralSecurityException e) {
			throw new DataAccessException("Failed to hash password", e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
		
		final User createdUser = new User(user);
		createdUser.setUuid(uuid);
		
		// for security purposes, don't return the user's password
		createdUser.setPassword(null);
		
		return createdUser;
	}
	
	/**
	 * Hash a password using the Blowfish cipher, random salt, and a configured cost factor
	 * 
	 * @param password the password to hash
	 * @return the hashed value, with the cost factor and salt appended
	 * @throws GeneralSecurityException if generating the hash fails
	 */
	private String hashPassword(final String password) throws GeneralSecurityException {
		
		final byte[] salt = new byte[16];
		
		SecureRandom.getInstanceStrong().nextBytes(salt);
		
		final int cost = securityConfiguration.getHashCost();
		
		final String hash = Base64.getEncoder().encodeToString(BCrypt.generate(password.getBytes(), salt, securityConfiguration.getHashCost()));
		
		return String.format("$%s$%s$%s", cost, Base64.getEncoder().encodeToString(salt), hash);
	}
	
	/**
	 * Check a user's password against a hashed password entry from the database
	 * 
	 * @param password user password entered
	 * @param hashedPassword hashed password from database
	 * @return true if passwords match, false if not
	 */
	private boolean checkPassword(final String password, final String hashedPassword) {
		final String[] parts = hashedPassword.split("\\$");
		
		final int cost = Integer.parseInt(parts[1]);
		
		final byte[] salt = Base64.getDecoder().decode(parts[2]);
		
		final byte[] hashedCheckPassword = BCrypt.generate(password.getBytes(), salt, cost);
		
		return parts[3].equals(Base64.getEncoder().encodeToString(hashedCheckPassword));
	}

	/**
	 * Read users from a result set. The result set will be closed before the method
	 * completes execution.
	 * 
	 * @param resultSet the SQL result set to read from
	 * @return the list of users retrieved
	 * @throws DataAccessException if reading from the result set failed.
	 */
	private List<User> readUsersFromResultSet(final ResultSet resultSet) throws DataAccessException {
		final List<User> users = new ArrayList<User>();
		
		try {
			while (resultSet.next()) {
				final User user = new User();
				user.setUuid((UUID) resultSet.getObject("uuid"));
				user.setEmail(resultSet.getString("email"));
				user.setPassword(resultSet.getString("password"));
				user.setGivenName(resultSet.getString("givenName"));
				user.setPhone(resultSet.getString("phone"));
				user.setSurname(resultSet.getString("surname"));
				
				users.add(user);
			}
		} catch (final SQLException e) {
			LOGGER.error("SQL error state: " + e.getSQLState());
			throw new DataAccessException("Failed to read user from result set", e);
		} finally {
			DbUtils.closeQuietly(resultSet);
		}
		
		return users;
	}
	
	/**
	 * Build an SQL connection. Don't forget to close it after you're done!
	 * 
	 * @return a new database connection
	 * @throws DataAccessException error occurred building the connection
	 */
	private Connection buildConnection() throws DataAccessException {
		
		Connection connection;
		try {
			connection = datasource.getConnection();
		} catch (final SQLException e) {
			LOGGER.error("SQL error state: " + e.getSQLState());
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		return connection;
	}
	
	/**
	 * Run a database query using a prepared statement and bound variables
	 * 
	 * @param connection the database connection to run the query against
	 * @param query the query to run
	 * @param boundData the data to bind to the prepared statement
	 * @param isQuery true if the statement is a query and is expected to return a result set (e.g. a SELECT statement)
	 * @return the result set from the query
	 * @throws DataAccessException error occurred running the query. The connection will be closed before the exception is thrown
	 */
	private ResultSet runStatement(final Connection connection,  final String query, List<Object> boundData, final boolean isQuery) throws DataAccessException {
		return runStatement(connection, query, boundData, isQuery, new IHandlesSqlErrors() {

			/**
			 * Handle an error
			 * 
			 * @param e the exception
			 * @throws DataAccessException not actually thrown here
			 */
			@Override
			public void handleError(final SQLException e) throws DataAccessException {
				// no handling
			}
			
		});
	}
	
	/**
	 * Run a database query using a prepared statement and bound variables
	 * 
	 * @param connection the database connection to run the query against
	 * @param query the query to run
	 * @param boundData the data to bind to the prepared statement
	 * @param queryErrorHandler handle an error that occurs as a result of running the query
	 * @param isQuery true if the statement is a query and is expected to return a result set (e.g. a SELECT statement)
	 * @return the result set from the query
	 * @throws DataAccessException error occurred running the query. The connection will be closed before the exception is thrown
	 */
	private ResultSet runStatement(final Connection connection, final String query, List<Object> boundData, final boolean isQuery, final IHandlesSqlErrors queryErrorHandler) throws DataAccessException {
		
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement(query);
		} catch (final SQLException e) {
			LOGGER.error("SQL error state: " + e.getSQLState());
			DbUtils.closeQuietly(connection);
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		ResultSet resultSet = null;
		try {
			int parameterIndex = 1;
			for (final Object boundVar : boundData) {
				setBoundVariable(statement, parameterIndex, boundVar);
				parameterIndex++;
			}
			
			if (isQuery) {
				resultSet = statement.executeQuery();
			} else {
				statement.executeUpdate();
			}
			
		
		} catch (final SQLException e) {
			LOGGER.error("SQL error state: " + e.getSQLState());
			queryErrorHandler.handleError(e);
			DbUtils.closeQuietly(connection);
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		return resultSet;
	}
	
	/**
	 * Bind a variable to a prepared statement
	 * 
	 * @param statement prepared SQL statement
	 * @param parameterIndex parameter index within the context of the bound statement
	 * @param boundVar the variable to bind to the prepared statement/query
	 * @throws SQLException if binding variables fails
	 */
	private void setBoundVariable(final PreparedStatement statement, final int parameterIndex, final Object boundVar) throws SQLException {
		if (boundVar instanceof String) {
			LOGGER.debug("Binding String variable: " + boundVar);
			statement.setString(parameterIndex, ((String)boundVar));
		} else {
			LOGGER.debug("Binding Object variable: " + boundVar);
			statement.setObject(parameterIndex, boundVar);
		}
	}
}
