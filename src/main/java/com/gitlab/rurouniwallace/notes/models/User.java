package com.gitlab.rurouniwallace.notes.models;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * A notes app user
 *
 */
public class User {
	
	/**
	 * The user's unique identifier
	 */
	@JsonProperty
	private UUID uuid;

	/**
	 * The user's email address
	 */
	@JsonProperty
	private String email;
	
	/**
	 * The user's password
	 */
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	
	/**
	 * The user's phone number
	 */
	@JsonProperty
	private String phone;
	
	/**
	 * The user's given name
	 */
	@JsonProperty
	private String givenName;
	
	/**
	 * The user's surname
	 */
	@JsonProperty
	private String surname;
	
	/**
	 * Construct a new instance
	 */
	public User() {
		// empty constructor to be used by Jackson serializer
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param email user email address
	 * @param password user password
	 * @param phone user phone number
	 * @param givenName user's given name
	 * @param surname user's surname
	 */
	public User(final String email, final String password, final String phone, final String givenName, final String surname) {
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.givenName = givenName;
		this.surname = surname;
	}
	
	/**
	 * Construct a new instance
	 * 
	 * @param uuid the user's unique identifier
	 * @param email user email address
	 * @param password user password
	 * @param phone user phone number
	 * @param givenName user's given name
	 * @param surname user's surname
	 */
	public User(final UUID uuid, final String email, final String password, final String phone, final String givenName, final String surname) {
		this.uuid = uuid;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.givenName = givenName;
		this.surname = surname;
	}
	
	/**
	 * Clone from another instance
	 * 
	 * @param other the other instance
	 */
	public User(final User other) {
		this.uuid = other.uuid;
		this.email = other.email;
		this.password = other.password;
		this.phone = other.phone;
		this.givenName = other.givenName;
		this.surname = other.surname;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @param givenName the givenName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	/**
	 * Check against another instance for equality
	 * 
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof User)) {
			return false;
		}
		
		return Objects.deepEquals(((User)other).uuid, this.uuid)
				&& Objects.deepEquals(((User)other).email, this.email)
				&& Objects.deepEquals(((User)other).password, this.password)
				&& Objects.deepEquals(((User)other).phone, this.phone)
				&& Objects.deepEquals(((User)other).givenName, this.givenName)
				&& Objects.deepEquals(((User)other).surname, this.surname);
	}
	
	/**
	 * Write as a string
	 * 
	 * @return instance as a string
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		final String newLine = System.getProperty("line.separator");
		
		result.append("User {" + newLine);
		result.append("\tuuid: " + uuid + newLine);
		result.append("\temail: " + email + newLine);
		result.append("\tpassword: " + password + newLine);
		result.append("\tphone: " + phone + newLine);
		result.append("\tgivenName: " + givenName + newLine);
		result.append("\tsurname: " + surname + newLine);
		result.append("}");
		
		return result.toString();
	}
}
