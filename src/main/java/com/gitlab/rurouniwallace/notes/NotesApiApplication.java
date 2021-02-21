package com.gitlab.rurouniwallace.notes;

import io.dropwizard.Application;
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
        // TODO: application initialization
    }

    @Override
    public void run(final NotesApiConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }
}
