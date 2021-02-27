package com.gitlab.rurouniwallace.notes.command;

import com.gitlab.rurouniwallace.notes.dao.IAccessesUsers;
import com.gitlab.rurouniwallace.notes.exceptions.EntityAlreadyExistsException;
import com.gitlab.rurouniwallace.notes.models.User;
import com.gitlab.rurouniwallace.notes.responses.StatusCode;
import com.gitlab.rurouniwallace.notes.responses.UserResponse;
import com.gitlab.rurouniwallace.notes.tenacity.NotesApiDependencyKeys;
import com.yammer.tenacity.core.TenacityCommand;

/**
 * Tenacity command to create a user
 */
public class CreateUserCommand extends TenacityCommand<UserResponse> {

	/**
	 * User persistence layer
	 */
	private final IAccessesUsers userDao;
	
	/**
	 * User to create
	 */
	private final User userToCreate;
	
	/**
	 * Construct a new instance
	 * 
	 * @param userDao user persistence layer
	 * @param userToCreate user to create
	 */
	public CreateUserCommand(final IAccessesUsers userDao, final User userToCreate) {
		super(NotesApiDependencyKeys.SQL_DB);
		this.userDao = userDao;
		this.userToCreate = userToCreate;
	}

	/**
	 * Execute the command
	 */
	@Override
	protected UserResponse run() throws Exception {
		User user;
		try {
			user = userDao.registerUser(userToCreate);
		} catch (final EntityAlreadyExistsException e) {
			return new UserResponse("A user with the specified email address already exists", StatusCode.ENTITY_ALREADY_EXISTS);
		}
		
		return new UserResponse(user, StatusCode.SUCCESS);
	}
}
