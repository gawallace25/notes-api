package com.gitlab.rurouniwallace.notes.command;

import com.gitlab.rurouniwallace.notes.dao.IAccessesUsers;
import com.gitlab.rurouniwallace.notes.exceptions.AuthenticationDeniedException;
import com.gitlab.rurouniwallace.notes.models.User;
import com.gitlab.rurouniwallace.notes.responses.StatusCode;
import com.gitlab.rurouniwallace.notes.responses.UserResponse;
import com.gitlab.rurouniwallace.notes.tenacity.NotesApiDependencyKeys;
import com.yammer.tenacity.core.TenacityCommand;

/**
 * Tenacity command to authenticate a user
 *
 */
public class AuthenticateUserCommand extends TenacityCommand<UserResponse> {

	/**
	 * User access DAO
	 */
	final IAccessesUsers userDao;
	
	/**
	 * User email address
	 */
	final String email;
	
	/**
	 * User password
	 */
	final String password;
	
	/**
	 * Construct a new instance
	 * 
	 * @param userDao user access DAO
	 * @param email user email address
	 * @param password user password
	 */
	public AuthenticateUserCommand(final IAccessesUsers userDao, final String email, final String password) {
		super(NotesApiDependencyKeys.SQL_DB);
		this.userDao = userDao;
		this.email = email;
		this.password = password;
	}

	/**
	 * Execute the command
	 */
	@Override
	protected UserResponse run() throws Exception {
		User user;
		try {
			user = userDao.authenticateUser(email, password);
		} catch (final AuthenticationDeniedException e) {
			return new UserResponse("Authentication failed", StatusCode.DENY);
		}
		
		return new UserResponse(user, StatusCode.ALLOW);
	}
}
