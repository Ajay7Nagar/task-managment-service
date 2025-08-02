# Installation Guide

## Prerequisites

- **Java 11 or higher**
- **Docker and Docker Compose**
- **Maven 3.6+** (optional, for development)

## Quick Start (Recommended)

### 1. Clone and Start
```bash
git clone <repository-url>
cd task-management-service
./start.sh
```

### 2. Create Admin User
After services are running, create the first admin user:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@taskmanagement.com", 
    "password": "admin123",
    "firstName": "System",
    "lastName": "Administrator",
    "role": "ADMIN"
  }'
```

### 3. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'
```

## Manual Installation

### Step 1: Database Setup
```bash
docker run -d --name postgres \
  -e POSTGRES_DB=taskmanagement_dev \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -p 5432:5432 postgres:13-alpine
```

### Step 2: Build and Run Application
```bash
mvn clean package -DskipTests
java -jar target/task-management-service-*.jar
```

## Production Deployment

### 1. Environment Configuration
```bash
cp .env.example .env
# Edit .env with your production values
```

### 2. Deploy with Docker Compose
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Development Setup

### 1. Start PostgreSQL
```bash
docker run -d --name postgres-dev \
  -e POSTGRES_DB=taskmanagement_dev \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -p 5432:5432 postgres:13-alpine
```

### 2. Run in Development Mode
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Verification

### Check Health
```bash
curl http://localhost:8080/api/health
```

### Access Swagger UI
Visit: http://localhost:8080/swagger-ui.html

### Test Database Connection
```bash
docker exec -it postgres psql -U taskuser -d taskmanagement_dev -c "\dt"
```

## Troubleshooting

### Service Won't Start
1. Check if ports 8080 and 5432 are available
2. Verify Docker is running: `docker info`
3. Check logs: `docker-compose logs`

### Database Connection Issues
1. Ensure PostgreSQL is running: `docker ps`
2. Check database credentials in configuration
3. Verify network connectivity: `docker network ls`

### Permission Issues
```bash
# Fix startup script permissions
chmod +x start.sh

# Fix Docker permissions (Linux)
sudo usermod -aG docker $USER
```

## Next Steps

1. **Create Users**: Use admin account to create other users
2. **API Testing**: Use Swagger UI or Postman
3. **Custom Configuration**: Modify application properties as needed
4. **Monitoring**: Set up logging and monitoring for production

For detailed API documentation, visit the Swagger UI at http://localhost:8080/swagger-ui.html