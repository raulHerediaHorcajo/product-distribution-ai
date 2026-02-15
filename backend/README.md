# Backend

This project is built with [Spring Boot](https://spring.io/projects/spring-boot).

## Development server

Run `mvn spring-boot:run` for a local server. The application will be available at `http://localhost:8080`.

You can also run with a specific profile:
- `mvn spring-boot:run -Dspring-boot.run.profiles=local` - Local development
- `mvn spring-boot:run -Dspring-boot.run.profiles=dev` - Development profile

## Build

Run `mvn clean package` to build the project. The build artifacts will be stored in the `target/` directory.

## Running tests

Run `mvn test` to execute the unit tests.

## Further help

For more information about Spring Boot, visit the [Spring Boot documentation](https://spring.io/projects/spring-boot).
