package com.gitlab.rurouniwallace.notes;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.gitlab.rurouniwallace.notes.config.NotesApiConfiguration;
import com.gitlab.rurouniwallace.notes.config.SqlFactory;
import com.gitlab.rurouniwallace.notes.config.YamlFileConfigurationSourceProvider;
import com.gitlab.rurouniwallace.notes.controllers.UserController;
import com.gitlab.rurouniwallace.notes.dao.IAccessesUsers;
import com.gitlab.rurouniwallace.notes.dao.SqlDao;
import com.gitlab.rurouniwallace.notes.resources.HealthResource;
import com.gitlab.rurouniwallace.notes.resources.UserResource;
import com.gitlab.rurouniwallace.notes.tenacity.NotesApiTenacityBundleConfigurationFactory;
import com.yammer.tenacity.core.bundle.TenacityBundleBuilder;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

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
    
    	bootstrap.addBundle(new SwaggerBundle<NotesApiConfiguration> () {

			@Override
			protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(final NotesApiConfiguration configuration) {
				return configuration.getSwagger();
			}
    	});
    	
    	bootstrap.addBundle(TenacityBundleBuilder.<NotesApiConfiguration>newBuilder().configurationFactory(new NotesApiTenacityBundleConfigurationFactory()).build());
    }

    @Override
    public void run(final NotesApiConfiguration configuration,
                    final Environment environment) throws SQLException, LiquibaseException {
    	
        final HealthResource healthResource = new HealthResource(environment);
        environment.jersey().register(healthResource);
        
        final SqlFactory sqlFactory = configuration.getSql();
        
        final DataSource sqlDataSource = sqlFactory.buildDataSource();
        
        // since right now we're using an in-memory database, we'll use Liquibase to
        // bootstrap the database at runtime
        initLiquibase(sqlDataSource);
        
        final IAccessesUsers userDao = new SqlDao(sqlDataSource, configuration.getSecurity());
        final UserController userController = new UserController(userDao);
        final UserResource userResource = new UserResource(userController);
        environment.jersey().register(userResource);
    }
    
    private void initLiquibase(final DataSource sqlDataSource) throws SQLException, LiquibaseException {
    	final Connection connection = sqlDataSource.getConnection();
		
		final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
		
		final Liquibase liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
		liquibase.update(new Contexts(), new LabelExpression());
		liquibase.close();
    }
}
