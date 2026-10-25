# Social Networking Platform

A backend health check application built with Java and Spring Boot.

## Backend API Documentation

[Swagger UI](https://13.127.245.42:8000/swagger-ui/index.html)

## Tech Stack

- **Backend:** Java, Spring Boot
- **Database:** MySQL
- **Authentication:** JWT, Role-Based Authorization
- **DevOps & Deployment:** Docker, Jenkins, AWS EC2

## Features

- User authentication and authorization
- Developed REST APIs for patient and doctor management.
- Integrated OpenAI using Spring AI for AI-powered functionality.
- REST API integration between frontend and backend

## API Testing

1. Open the [Swagger UI](https://13.127.245.42:8000/swagger-ui/index.html).
2. Select an API endpoint.
3. Review the request parameters and response schema.
4. Click **Try it out** to test the endpoint.

## Build and Run

### Pre-Requisites
- Java 17
- Maven
- MySQL

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd Health
```

### 2. Configure the Database
Update `src/main/resources/application.properties` with your MySQL database credentials.

### 3. Build the Application
```bash
mvn clean package
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

Alternatively, run the generated JAR:
```bash
java -jar target/check-0.0.1-SNAPSHOT.jar
```

### 5. Access Swagger UI
Once the application is running, open:

http://localhost:8000/swagger-ui/index.html

**Note:** The Swagger UI requires the backend server to be running and accessible.
