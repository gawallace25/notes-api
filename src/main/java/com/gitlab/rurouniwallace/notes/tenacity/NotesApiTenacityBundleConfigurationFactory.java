package com.gitlab.rurouniwallace.notes.tenacity;

import java.util.Map;

import com.gitlab.rurouniwallace.notes.config.NotesApiConfiguration;
import com.gitlab.rurouniwallace.notes.config.UpstreamTenacityConfiguration;
import com.google.common.collect.ImmutableMap;
import com.yammer.tenacity.core.bundle.BaseTenacityBundleConfigurationFactory;
import com.yammer.tenacity.core.bundle.TenacityBundleConfigurationFactory;
import com.yammer.tenacity.core.config.TenacityConfiguration;
import com.yammer.tenacity.core.properties.TenacityPropertyKey;
import com.yammer.tenacity.core.properties.TenacityPropertyKeyFactory;

import io.dropwizard.Configuration;

/**
 * Tenacity key configurations registry
 *
 */
public class NotesApiTenacityBundleConfigurationFactory extends BaseTenacityBundleConfigurationFactory<NotesApiConfiguration>  {

	/**
	 * Dependency key factory
	 */
	private final NotesApiDependencyKeyFactory keyFactory;
	
	/**
	 * Construct a new instance
	 */
	public NotesApiTenacityBundleConfigurationFactory() {
		keyFactory = new NotesApiDependencyKeyFactory();
	}
	
	/**
	 * Builds a mapping between Tenacity keys and Tenacity configurations
	 * 
	 * @param configuration application configuration
	 */
	@Override
	public Map<TenacityPropertyKey, TenacityConfiguration> getTenacityConfigurations(final NotesApiConfiguration configuration) {
final ImmutableMap.Builder<TenacityPropertyKey, TenacityConfiguration> builder = ImmutableMap.builder();
		
		final UpstreamTenacityConfiguration upstreamTenacityConfig = configuration.getUpstreamTenacity();
		
		builder.put(NotesApiDependencyKeys.HEALTH, upstreamTenacityConfig.getHealth());
	
		return builder.build();
	}
	
	/**
	 * Builds a mapping between Tenacity keys and Tenacity configurations
	 * 
	 * @param configuration application configuration
	 * @return Tenacity key to configuration mapping
	 */
	@Override
	public TenacityPropertyKeyFactory getTenacityPropertyKeyFactory(NotesApiConfiguration applicationConfiguration) {
		
		return keyFactory;
	}

}
