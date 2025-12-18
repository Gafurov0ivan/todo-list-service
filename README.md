## Service Description

#### Backend service for managing a simple to-do list:

- Create, Read, Update, API operations for todo items based on requirements.
- H2 in-memory database for data persistence.
- Scheduled job to automatically change an item's status to "past due" every minute.
- API documentation using Swagger.
- Automatic and integration tests using JUnit 5 and Mockito.

#### Business requirements assumptions:

1. Update the todo list can only one user/request.
   On concurrent updates I would extend the solution to use fine grain locks or optimistic locking with versioning.
2. I chose ISO-8601 (YYYY-MM-DDTHH:mm:ss.sssZ) date format for the API, because specific format was not mentioned in the
   requirements.
3. I chose the UTC timezone for all dates in the system and API.
4. I'm using the next status format "not done", as in the requirements.

#### Non-functional trade-offs:

1. I didn't add the database migration tool, because it was not in the requirements.
   In a real-world project, I would use Flyway or Liquibase for database versioning and migrations.
2. I'm using Spotless with Google Java Format for code formatting and consistency.
3. For the integration tests I created util logic, which can help to extend test cases easily in the future.
   On the other hand it adds some complexity to the test code.

#### Architecture:

1. Layered architecture
2. Balanced DDD architecture with clear separation of domain, infrastructure and api layers.
   But with Spring Boot dependencies in the domain layer.
   I didn't go full DDD or Hexagonal to keep the solution simple and focused on the requirements.

## Tech Stack

- Java 21
- Spring Boot
- Gradle
- H2 Database
- Swagger
- Lombok
- Docker
- JUnit 5
- Mockito
- Spotless (using Google Java Format)

## How-To Guide:

### Run using Docker:

Prerequisites: Docker is installed and running.

   ```bash
     docker-compose up
   ```

### Build the service:

Prerequisites: Java 21 must be installed

   ```bash
     ./gradlew clean spotlessApply build
   ```

### Run the automatic tests:

Prerequisites: Java 21 must be installed

   ```bash
     ./gradlew test --tests com.gafur.todo.api.controller.TodoItemIntegrationTest
   ```

## API Swagger docs:

http://localhost:8080/swagger-ui/index.html

## Security Note

This project uses a simple database password (`password`) for local testing.
In production, credentials would be managed using Environment variables.
