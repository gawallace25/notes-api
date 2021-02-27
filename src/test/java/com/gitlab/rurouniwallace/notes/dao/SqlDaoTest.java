package com.gitlab.rurouniwallace.notes.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.bouncycastle.crypto.generators.BCrypt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import com.gitlab.rurouniwallace.notes.config.SecurityConfiguration;
import com.gitlab.rurouniwallace.notes.exceptions.AuthenticationDeniedException;
import com.gitlab.rurouniwallace.notes.exceptions.AuthenticationException;
import com.gitlab.rurouniwallace.notes.exceptions.DataAccessException;
import com.gitlab.rurouniwallace.notes.exceptions.EntityAlreadyExistsException;
import com.gitlab.rurouniwallace.notes.models.User;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class SqlDaoTest {
	
	private static final String MOCK_CONNECTION_URL = "jdbc:hsqldb:mem:myDb;sql.sql.syntax_pgs=true";
	
	/**
	 * Database connection source
	 */
	private static BasicDataSource dataSource;
	
	private SecurityConfiguration securityConfig;
	
	@BeforeAll
	private static void setUpBeforeClass() throws SQLException, LiquibaseException, ClassNotFoundException {
		
		dataSource = new BasicDataSource();
		dataSource.setUrl(MOCK_CONNECTION_URL);
		
		final Connection connection = dataSource.getConnection();
		
		final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
		
		final Liquibase liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
		liquibase.update(new Contexts(), new LabelExpression());
		liquibase.close();
	}
	
	@BeforeEach
	public void setUp() {
		securityConfig = new SecurityConfiguration();
		securityConfig.setHashCost(5);
	}

	@Test
	public void registerUser_InsertSuccess_InsertedUserMatchesOriginal() throws DataAccessException, SQLException {
		final SqlDao dao = new SqlDao(dataSource, securityConfig);
		
		final User userToRegister = new User("testuser1@example.com", "test1234", "(716)888-8888", "Testy", "Testerson");
		
		final User createdUser = dao.registerUser(userToRegister);
		
		final User userInDatabase = getUserFromDatabase(createdUser.getUuid());
		
		// we want to ignore the UUID. In another test case we'll check to verify that a UUID was
		// generated
		userToRegister.setUuid(null);
		userInDatabase.setUuid(null);
		
		// ignore the password. In another test case we'll check that it was hashed properly
		userToRegister.setPassword(null);
		userInDatabase.setPassword(null);
		
		assertEquals(userToRegister, userInDatabase, "User pulled from database after registration doesn't match what was expected");
	}
	
	
	@Test
	public void registerUser_InsertSuccess_PasswordhashedProperly() throws DataAccessException, SQLException {
		final SqlDao dao = new SqlDao(dataSource, securityConfig);
		
		final String password = "test1234";
		final User userToRegister = new User("testuser2@example.com", password, "(716)888-8888", "Testy", "Testerson");
		
		final User createdUser = dao.registerUser(userToRegister);
		
		final User userInDatabase = getUserFromDatabase(createdUser.getUuid());
		
		final String hashedPassword = userInDatabase.getPassword();
		
		final String[] parts = hashedPassword.split("\\$");
		
		final int costFactor = Integer.parseInt(parts[1]);
		final byte[] salt = Base64.getDecoder().decode(parts[2]);
		
		final String expectedHash = Base64.getEncoder().encodeToString(BCrypt.generate(password.getBytes(), salt, costFactor));
		final String hashFromDatabase = parts[3];
		
		assertEquals(expectedHash, hashFromDatabase, "Hash in database doesn't match what was expected");
	}
	
	@Test
	public void registerUser_UserEmailAlreadyExists_ThrowException() throws DataAccessException, SQLException {
		final SqlDao dao = new SqlDao(dataSource, securityConfig);
		
		insertUserIntoDatabase(new User(UUID.randomUUID(), "testuser3@example.com", "(716)888-8888", "test1234", "Testy", "Testerson"));
		
		assertThrows(EntityAlreadyExistsException.class, () -> {
			dao.registerUser(new User("testuser3@example.com", "(716)854-2020", "abcd1234", "Another", "Tester"));
		});
	}
	
	@Test
	public void authenticateUser_EmailAndPasswordCorrect_ReturnUser() throws SQLException, DataAccessException, AuthenticationException, GeneralSecurityException {
		final SqlDao dao = new SqlDao(dataSource, securityConfig);
		
		final String email = "testuser4@example.com";
		final String password = "test1234";
		
		final User user = new User(UUID.randomUUID(), email, hashPassword(password), "(716)888-8888", "Testy", "Testerson");
		insertUserIntoDatabase(user);
		
		final User userFromDao = dao.authenticateUser(email, password);
		
		assertEquals(user, userFromDao);
	}
	
	@Test
	public void authenticateUser_EmailAddressNotFound_ThrowAuthenticationDeniedException() {
		final SqlDao dao = new SqlDao(dataSource, securityConfig);
		
		final String email = "nonexistent@example.com";
		final String password = "test1234";
		
		assertThrows(AuthenticationDeniedException.class, () -> {
			dao.authenticateUser("nonexistent@example.com", "test1234");
		});
	}
	
	@Test
	public void authenticateUser_PasswordIncorrect_ThrowAuthenticationDeniedException() throws GeneralSecurityException, SQLException, DataAccessException, AuthenticationException {
		final SqlDao dao = new SqlDao(dataSource, securityConfig);
		
		final String email = "testuser5@example.com";
		final String password = "test1234";
		
		final User user = new User(UUID.randomUUID(), email, hashPassword(password), "(716)888-8888", "Testy", "Testerson");
		insertUserIntoDatabase(user);
		
		assertThrows(AuthenticationDeniedException.class, () -> {
			dao.authenticateUser(email, "different password");
		});
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
		
		final int cost = securityConfig.getHashCost();
		
		final String hash = Base64.getEncoder().encodeToString(BCrypt.generate(password.getBytes(), salt, securityConfig.getHashCost()));
		
		return String.format("$%s$%s$%s", cost, Base64.getEncoder().encodeToString(salt), hash);
	}
	
	private void insertUserIntoDatabase(final User user) throws SQLException {
		final Connection connection = dataSource.getConnection();
		
		final PreparedStatement insertUserStatement = connection.prepareStatement("INSERT INTO Users (uuid, email, password, phone, givenName, surname) VALUES (?, ?, ?, ?, ?, ?)");
		insertUserStatement.setObject(1, user.getUuid());
		insertUserStatement.setString(2, user.getEmail());
		insertUserStatement.setString(3, user.getPassword());
		insertUserStatement.setString(4, user.getPhone());
		insertUserStatement.setString(5, user.getGivenName());
		insertUserStatement.setString(6, user.getSurname());
		
		insertUserStatement.executeUpdate();
	}
	
	private User getUserFromDatabase(final UUID uuid) throws SQLException {
		final Connection connection = dataSource.getConnection();
		
		final PreparedStatement getUserStatement = connection.prepareStatement("SELECT * FROM Users WHERE uuid = ?");
		getUserStatement.setObject(1, uuid);
		

		final ResultSet queryResults = getUserStatement.executeQuery();
		
		queryResults.next();
		
		final User userFromDatabase = new User();
		
		userFromDatabase.setUuid((UUID)queryResults.getObject("uuid"));
		userFromDatabase.setEmail(queryResults.getString("email"));
		userFromDatabase.setPassword(queryResults.getString("password"));
		userFromDatabase.setPhone(queryResults.getString("phone"));
		userFromDatabase.setGivenName(queryResults.getString("givenName"));
		userFromDatabase.setSurname(queryResults.getString("surname"));
		
		return userFromDatabase;
	}
}
