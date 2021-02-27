package com.gitlab.rurouniwallace.notes.dao;

import java.util.Optional;

import com.gitlab.rurouniwallace.notes.exceptions.AuthenticationException;
import com.gitlab.rurouniwallace.notes.exceptions.DataAccessException;
import com.gitlab.rurouniwallace.notes.models.User;

/**
 * Data access object that accesses user data
 *
 */
public interface IAccessesUsers {
	
	/**
	 * Authenticate a user with their email and password
	 * 
	 * @param email user email address
	 * @param password user password
	 * @return the user data
	 * @throws DataAccessException error occurred accessing database
	 * @throws AuthenticationException  authentication failed
	 */
	public User authenticateUser(final String email, final String password) throws DataAccessException, AuthenticationException;

	/**
	 * Registers a new user
	 * 
	 * @param user the user to register
	 * @return the created user
	 * @throws DataAccessException error occurred accessing datasource
	 */
	public User registerUser(final User user)  throws DataAccessException;
}
