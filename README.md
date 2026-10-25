# Health Check Application

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



## Build and Deploy to Production

The application is built and deployed using Jenkins, Docker, and AWS EC2.

### Prerequisites

- Jenkins configured with Maven and required credentials.
- AWS EC2 instance with Docker and Docker Compose installed.
- Application repository available on EC2.
- `docker-compose.yml` configured in the `~/Health` directory.

### 1. Checkout Source Code

Jenkins checks out the application source code from the configured Git repository.

### 2. Build the Application

Jenkins builds the Spring Boot application using Maven.

```bash
mvn clean install
```

The generated JAR is:

```text
target/check-0.0.1-SNAPSHOT.jar
```

### 3. Configure Environment Variables

Jenkins retrieves sensitive values from Jenkins Credentials, including:

- Database username and password
- JWT secret
- OpenAI API key
- SSL keystore password
- EC2 SSH private key

The pipeline creates an `.env` file containing the required application configuration.

### 4. Transfer Files to EC2

Jenkins uses SCP to transfer the generated JAR and `.env` file to the EC2 instance.

### 5. Deploy Using Docker Compose

Jenkins connects to EC2 over SSH and executes the deployment commands.

```bash
cd ~/Health
mkdir -p target
git pull
docker compose down
docker compose up -d --build
```

The new JAR is placed in the `target` directory before the Docker deployment is rebuilt.

### 6. Verify Deployment

Check the running containers:

```bash
docker compose ps
```

### 7. Access the Application

Swagger UI:

http://<your-ec2-host>:8000/swagger-ui/index.html

Replace `<your-ec2-host>` with your EC2 public IP or hostname.

### Security Notes

- Store secrets in Jenkins Credentials instead of hardcoding them.
- Do not commit `.env` files or private keys to Git.
- Restrict access to application ports and SSH using AWS Security Groups.
