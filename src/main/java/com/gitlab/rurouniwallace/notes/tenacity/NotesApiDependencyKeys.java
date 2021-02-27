package com.gitlab.rurouniwallace.notes.tenacity;

import com.yammer.tenacity.core.properties.TenacityPropertyKey;

/**
 * Tenacity Dependency keys
 *
 */
public enum NotesApiDependencyKeys implements TenacityPropertyKey {
	HEALTH, SQL_DB
}
