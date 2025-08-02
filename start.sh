#!/bin/bash

# Task Management Service Startup Script

echo "🚀 Starting Task Management Service..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

echo "✅ Docker is running"

# Build and start services
echo "🔧 Building and starting services..."
docker-compose up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check if services are running
if docker-compose ps | grep -q "Up"; then
    echo "✅ Services are running!"
    echo ""
    echo "📍 Access Points:"
    echo "   • API: http://localhost:8080"
    echo "   • Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   • Health Check: http://localhost:8080/api/health"
    echo "   • PgAdmin: http://localhost:5050 (admin@taskmanagement.com / admin123)"
    echo ""
    echo "🔑 To create an admin user, make a POST request to:"
    echo "   http://localhost:8080/api/auth/register"
    echo ""
    echo "📝 Example admin user creation:"
    echo '   curl -X POST http://localhost:8080/api/auth/register \'
    echo '   -H "Content-Type: application/json" \'
    echo '   -d "{'
    echo '     \"username\": \"admin\",'
    echo '     \"email\": \"admin@taskmanagement.com\",'
    echo '     \"password\": \"admin123\",'
    echo '     \"firstName\": \"System\",'
    echo '     \"lastName\": \"Administrator\",'
    echo '     \"role\": \"ADMIN\"'
    echo '   }"'
else
    echo "❌ Some services failed to start. Check logs with: docker-compose logs"
    exit 1
fi