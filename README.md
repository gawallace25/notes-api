# Notes API

REST API for an app that allows a user to take and manage notes

## Build Instructions

The codebase can be built using the gradle wrapper embedded in the project root directory:

### -Nix Systems (Linux, OS X)
`./gradlew init`

`./gradlew build`

### Windows

TBD

## Start The Service!

Once the executable has been build, you can run the service on your local machine. The service will
be hosted over port 8080 of your localhost.

`./gradlew run --args="server config.yml"`

### Health Checks
To check that the server is running, open the following URL in either a browser tab, curl, or your preferred REST client:

[http://localhost:8080/health](http://localhost:8080/health)

### Swagger Documentation
All API operations are documented using [Swagger.io](https://swagger.io/solutions/api-documentation/).
Once the server is running, you can navigate to the Swagger documentation and test out all of the service's API operations with a handy UI:

[http://localhost:8080/swagger](http://localhost:8080/swagger)

## Automated Tests

Unit tests have been written for select modules of code using JUnit 5. The can be run via the gradle wrapper:

`./gradlew test`