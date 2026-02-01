# RevaIssue

> RevaIssue is a full stack issue tracking application built with Spring Boot and Angular. It supports role based workflows for Admins, Testers, and Developers to manage projects and track issues from creation through resolution.

RevaIssue was designed to model a real world internal issue tracking system. The focus of the project is not only on functionality, but also on clean architecture, enforceable business rules, and collaboration within a shared codebase.

## Why This Project Exists

This project was built as a team exercise to practice designing and implementing a production style application from the ground up.

The primary goals were:

- Modeling real user roles and permissions
- Enforcing workflow rules through backend services
- Designing clean REST APIs
- Coordinating development using shared standards and structure
- Building a true full stack application using Angular and Spring Boot

## Core Features

### User Roles

- **Admin**
  - Creates and manages projects
  - Assigns Testers and Developers to projects

- **Tester**
  - Creates new issues when defects are found
  - Reopens or closes issues after validation

- **Developer**
  - Moves issues to In Progress and Resolved while implementing fixes

### Project Management
- Create, update, and view projects
- Assign users to projects
- View issues associated with each project

### Issue Tracking
- Create, update, and view issues
- Track issue state transitions
- Support severity, priority, and comment history
- Automatically log all issue and project actions for auditing

## Tech Stack

### Backend
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Lombok
- Gradle 9.2.1

### Persistence
- SQLite for first sprint persistence

### Frontend
- Angular
- Client side authentication
- Dashboards for projects and issues

## Project Structure

```bash
src/
├── main/
│ ├── java/
│ │ └── com/abra/revaissue/
│ │ ├── controller
│ │ ├── entity
│ │ ├── enums
│ │ ├── repository
│ │ └── service
│ └── resources/
│ └── application.yml
└── test/
```

### Structure Rationale

The application follows standard Spring Boot conventions.

- Controllers handle HTTP requests and responses
- Services enforce business rules and workflows
- Repositories manage persistence
- Entities represent domain models
- Enums model fixed domain states such as roles and issue statuses

This separation keeps the codebase readable and maintainable.

## Team Standards and Decisions

- Base package is `com.abra.revaissue`
- Application entry point is `RevaIssueApplication.java`
- Gradle is used as the build tool for flexibility and performance
- Business logic is enforced in the service layer, not controllers
- SQLite is used during early development to reduce setup overhead
- DBeaver is used to inspect tables and validate schema structure

## Running the Application

### Prerequisites
- Java 17 or later
- Gradle

### Start the backend

From the `abra-studio/RevaIssue` directory, run:
```bash
./gradlew bootRun
```

The application will be available at:
```bash
http://localhost:8081
```

## API Overview

### Users
- Authentication using JWT
- Assigning Testers and Developers to projects

### Projects
- Create, update, and view projects

### Issues
- Create, update, and view issues
- Enforce role based workflow transitions
- Log all actions for audit purposes

## Audit Logging

All project and issue state changes are logged automatically. This allows Admins to review who performed an action, what changed, and when it occurred. This feature models accountability commonly required in production systems.

## Current Status and Next Steps

The application is actively under development.

Planned improvements include:
- Completing JWT authentication flows
- Expanding audit log visibility
- Improving validation and error handling
- Refining Angular UI flows
- Adding integration and service level tests

## Team

**Team Name:** ABRA-STUDIO

- Team Member 1: [Armando Arteaga]()
- Team Member 2: [Byron Sophin](https://github.com/ByronSophin)
- Team Member 3:
- Team Member 4:

## References

### Gradle and Build Tooling
Resources related to build configuration, dependency management, and build optimization.

- [Gradle Official Documentation](https://docs.gradle.org)
- [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.0/gradle-plugin)
- [Gradle Build Scans](https://scans.gradle.com#gradle)
- [OCI Image Packaging with Gradle](https://docs.spring.io/spring-boot/4.0.0/gradle-plugin/packaging-oci-image.html)

---

### Spring Web and REST APIs
Resources related to building RESTful APIs and web endpoints.
- [Spring Web Reference Documentation](https://docs.spring.io/spring-boot/4.0.0/reference/web/servlet.html)
- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
- [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
- [Building REST Services with Spring](https://spring.io/guides/tutorials/rest/)

---

### Data Persistence and JPA
Resources focused on database interaction, ORM, and persistence design.
- [Spring Data JPA Reference Documentation](https://docs.spring.io/spring-boot/4.0.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
- [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)