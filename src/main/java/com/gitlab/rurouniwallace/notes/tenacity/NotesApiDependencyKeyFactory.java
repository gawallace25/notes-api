package com.gitlab.rurouniwallace.notes.tenacity;

import com.yammer.tenacity.core.properties.TenacityPropertyKey;
import com.yammer.tenacity.core.properties.TenacityPropertyKeyFactory;

/**
 * Manufactures a Tenacity property key from a string
 */
public class NotesApiDependencyKeyFactory implements TenacityPropertyKeyFactory {

	/**
	 * Get a Tenacity property key from a string.
	 */
	@Override
	public TenacityPropertyKey from(String value) {
		return NotesApiDependencyKeys.valueOf(value.toUpperCase());
	}

}
