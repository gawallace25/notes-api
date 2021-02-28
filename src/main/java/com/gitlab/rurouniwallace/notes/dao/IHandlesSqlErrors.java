package com.gitlab.rurouniwallace.notes.dao;

import java.sql.SQLException;

import com.gitlab.rurouniwallace.notes.exceptions.DataAccessException;

public interface IHandlesSqlErrors {

	/**
	 * Handle an SQL error
	 * 
	 * @param e exception to handle
	 * @throws DataAccessException possibly re-thrown SQL error
	 */
	public void handleError(final SQLException e) throws DataAccessException;
}
