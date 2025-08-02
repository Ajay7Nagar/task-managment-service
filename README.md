# Task Management Service

A comprehensive task management system built with Spring Boot, featuring workflow management, role-based access control, and RESTful APIs.

## Features

### Core Functionality
- **User Management**: User registration, authentication, and role-based access control
- **Task Management**: Create, read, update, and delete tasks with different types
- **Workflow Management**: Task status transitions with validation (Draft → To Do → In Progress → QA → Ready to Deploy → Done)
- **Role-Based Permissions**: Different user roles with specific capabilities

### Task Types
- **Epic**: High-level business objectives
- **Story**: User stories and requirements
- **Task**: Individual work items
- **Subtask**: Sub-components of stories
- **Spike**: Research and investigation tasks

### User Roles
- **Admin**: Full system access, user management
- **Manager**: Create epics, stories, tasks, and spikes; manage team assignments
- **Developer**: Create subtasks under assigned stories
- **Tester**: Create subtasks under assigned stories

### Technical Features
- **JWT Authentication**: Secure token-based authentication
- **Method-Level Security**: Fine-grained security with Spring Security annotations
- **Swagger Documentation**: Interactive API documentation
- **Docker Support**: Containerized deployment
- **PostgreSQL Database**: Robust data persistence
- **Comprehensive Testing**: Unit and integration tests

## Technology Stack

- **Java 11**
- **Spring Boot 2.5.14**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (JSON Web Tokens)**
- **Swagger/OpenAPI 3**
- **Docker & Docker Compose**
- **JUnit 5 & Mockito**
- **Maven**

## Quick Start

### Prerequisites
- Java 11 or higher
- Docker and Docker Compose
- Maven 3.6+

### Running with Docker Compose

1. Clone the repository:
```bash
git clone <repository-url>
cd task-management-service
```

2. Start the services:
```bash
docker-compose up -d
```

3. The application will be available at:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html#/
   - PgAdmin (optional): http://localhost:5050

### Running Locally

1. Start PostgreSQL database:
```bash
docker run -d --name postgres \
  -e POSTGRES_DB=taskmanagement_dev \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -p 5432:5432 postgres:13-alpine
```

2. Run the application:
```bash
mvn spring-boot:run
```

## API Documentation

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - Register new user (Admin only)
- `GET /api/auth/me` - Get current user info

### User Management Endpoints
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `PUT /api/users/{id}/activate` - Activate user
- `PUT /api/users/{id}/deactivate` - Deactivate user

### Task Management Endpoints
- `POST /api/tasks` - Create task
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `PUT /api/tasks/{id}/transition` - Transition task status
- `PUT /api/tasks/{taskId}/assign/{assigneeId}` - Assign task
- `DELETE /api/tasks/{id}` - Delete task

### Health Check
- `GET /api/health` - Health check endpoint

## Task Workflow

Tasks follow a strict workflow sequence:

```
Draft → To Do → In Progress → QA → Ready to Deploy → Done
```

**Allowed Transitions:**
- Draft → To Do
- To Do → In Progress
- In Progress → QA (or back to To Do)
- QA → Ready to Deploy (or back to In Progress)
- Ready to Deploy → Done (or back to QA)

## Role Permissions

### Task Creation Permissions
| Role      | Epic | Story | Task | Subtask | Spike |
|-----------|------|-------|------|---------|-------|
| Admin     | ✓    | ✓     | ✓    | ✓       | ✓     |
| Manager   | ✓    | ✓     | ✓    | ✓       | ✓     |
| Developer | ✗    | ✗     | ✗    | ✓*      | ✗     |
| Tester    | ✗    | ✗     | ✗    | ✓*      | ✗     |

*Only under assigned stories

### Task Management Permissions
- **Admin**: Full access to all tasks
- **Manager**: Can edit, assign, and delete any task
- **Developer/Tester**: Can edit assigned tasks and create subtasks under assigned stories

## Configuration

### Environment Variables

#### Development
```yaml
DATABASE_URL: jdbc:postgresql://localhost:5432/taskmanagement_dev
DATABASE_USERNAME: taskuser
DATABASE_PASSWORD: taskpass
JWT_SECRET: mySecretKey
JWT_EXPIRATION: 86400000
```

#### Production
```yaml
DATABASE_URL: jdbc:postgresql://localhost:5432/taskmanagement_prod
DATABASE_USERNAME: taskuser
DATABASE_PASSWORD: your_secure_password
JWT_SECRET: your_very_long_and_secure_secret_key
JWT_EXPIRATION: 86400000
SERVER_PORT: 8080
```

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
The project includes comprehensive tests covering:
- Service layer business logic
- Controller layer endpoints
- Security configurations
- Workflow validations

## Deployment

### Production Deployment
```bash
# Build and deploy with production configuration
docker-compose -f docker-compose.prod.yml up -d
```

### Environment Variables for Production
Create a `.env` file:
```
POSTGRES_DB=taskmanagement_prod
POSTGRES_USER=taskuser
POSTGRES_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_and_secure_secret_key
JWT_EXPIRATION=86400000
APP_PORT=8080
```

## Default Admin User

For initial setup, you can create an admin user through the registration endpoint or database script:

```json
{
  "username": "admin",
  "email": "admin@taskmanagement.com",
  "password": "admin123",
  "firstName": "System",
  "lastName": "Administrator",
  "role": "ADMIN"
}
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.