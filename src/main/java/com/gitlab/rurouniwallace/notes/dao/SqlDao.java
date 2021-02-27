package com.gitlab.rurouniwallace.notes.dao;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
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

public class SqlDao implements IAccessesUsers {
	
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
		Connection connection = null;
		try {
			connection = datasource.getConnection();
		} catch (final SQLException e) {
			DbUtils.closeQuietly(connection);
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		PreparedStatement lookupUserStatement = null;
		
		try {
			lookupUserStatement = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
		} catch (SQLException e) {
			DbUtils.closeQuietly(connection);
			LOGGER.error("SQL error state: " + e.getSQLState());
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		ResultSet resultSet = null;
		try {
			lookupUserStatement.setString(1, email);
			resultSet = lookupUserStatement.executeQuery();
		} catch (final SQLException e) {
			DbUtils.closeQuietly(connection);
			LOGGER.error("SQL error state: " + e.getSQLState());
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		boolean userFound;
		try {
			userFound = resultSet.next();
		} catch (SQLException e) {
			LOGGER.error("SQL error state: " + e.getSQLState());
			DbUtils.closeQuietly(connection);
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		if (!userFound) {
			throw new AuthenticationDeniedException("User not found");
		}
		
		User userFromDatabase;
		try {
			userFromDatabase = readUserFromResultSet(resultSet);
		} catch (SQLException e) {
			LOGGER.error("SQL error state: " + e.getSQLState());
			throw new DataAccessException("SQL Error Occurred", e);
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(resultSet);
		}
		
		final String hashedPassword = userFromDatabase.getPassword();
		
		if (!checkPassword(password, hashedPassword)) {
			throw new AuthenticationDeniedException("Passwords don't match");
		}
		
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
		
		Connection connection = null;
		try {
			connection = datasource.getConnection();
		} catch (final SQLException e) {
			DbUtils.closeQuietly(connection);
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		final UUID uuid = UUID.randomUUID();
		
		PreparedStatement insertUserStatement = null;
		
		try {
			insertUserStatement = connection.prepareStatement("INSERT INTO users(uuid, email, password, phone, givenName, surname) VALUES (?, ?, ?, ?, ?, ?)");
		} catch (final SQLException e) {
			DbUtils.closeQuietly(connection);
			LOGGER.error("SQL error state: " + e.getSQLState());
			throw new DataAccessException("SQL Error Occurred", e);
		}
		
		try {
			insertUserStatement.setObject(1, uuid);
			insertUserStatement.setString(2, user.getEmail());
			insertUserStatement.setString(3, hashPassword(user.getPassword()));
			insertUserStatement.setString(4, user.getPhone());
			insertUserStatement.setString(5, user.getGivenName());
			insertUserStatement.setString(6, user.getSurname());
			
			final int numRowsAffected = insertUserStatement.executeUpdate();
			LOGGER.debug("Rows affected: " + numRowsAffected);
		
		} catch (final SQLException e) {
			if (e.getSQLState().equals("23505")) {
				throw new EntityAlreadyExistsException("A user with the provided email already exists", e);
			}
			throw new DataAccessException("SQL Error Occurred", e);
		} catch (final GeneralSecurityException e) {
			throw new DataAccessException("Password hashing failed", e);
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
	 * Read a user from a JDBC result set
	 * 
	 * @param resultSet result set to read from
	 * @return user from result set
	 * @throws SQLException if an error occurred reading from result set
	 */
	private User readUserFromResultSet(final ResultSet resultSet) throws SQLException {
		final User user = new User();
		user.setUuid((UUID) resultSet.getObject("uuid"));
		user.setEmail(resultSet.getString("email"));
		user.setPassword(resultSet.getString("password"));
		user.setGivenName(resultSet.getString("givenName"));
		user.setPhone(resultSet.getString("phone"));
		user.setSurname(resultSet.getString("surname"));
		
		return user;
	}
}
