package com.gitlab.rurouniwallace.notes;

import com.gitlab.rurouniwallace.notes.config.NotesApiConfiguration;
import com.gitlab.rurouniwallace.notes.config.YamlFileConfigurationSourceProvider;
import com.gitlab.rurouniwallace.notes.resources.HealthResource;
import com.gitlab.rurouniwallace.notes.tenacity.NotesApiTenacityBundleConfigurationFactory;
import com.yammer.tenacity.core.bundle.TenacityBundleBuilder;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NotesApiApplication extends Application<NotesApiConfiguration> {

    public static void main(final String[] args) throws Exception {
        new NotesApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "NotesApi";
    }

    @Override
    public void initialize(final Bootstrap<NotesApiConfiguration> bootstrap) {
    	bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(new YamlFileConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    
    	bootstrap.addBundle(TenacityBundleBuilder.<NotesApiConfiguration>newBuilder().configurationFactory(new NotesApiTenacityBundleConfigurationFactory()).build());
    }

    @Override
    public void run(final NotesApiConfiguration configuration,
                    final Environment environment) {
        final HealthResource healthResource = new HealthResource(environment);
        
        environment.jersey().register(healthResource);
    }
}
