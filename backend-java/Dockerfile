# ==============================================================================
# Multi-stage Dockerfile for GrowEasy AI Importer (Java Spring Boot Backend)
# Optimized for deployment on Render
# ==============================================================================

# Stage 1: Build stage using Maven 3.9 and Eclipse Temurin Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy Maven descriptor and download/cache dependencies if unchanged
COPY pom.xml .

# Copy application source code
COPY src ./src

# Build the Spring Boot application artifact without running tests
RUN mvn -B clean package -DskipTests

# Stage 2: Lean runtime stage using Eclipse Temurin Java 21 JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the executable Spring Boot fat JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Ensure runtime directories for file uploads and Excel exports exist
RUN mkdir -p /app/exports /app/uploads

# Expose port (Render defaults to 10000)
EXPOSE 10000

# Start the Spring Boot application binding to 0.0.0.0 and dynamically binding to ${PORT:-10000}
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-10000} --server.address=0.0.0.0"]
