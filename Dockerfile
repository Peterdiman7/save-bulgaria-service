# ---------- STAGE 1: Build with Maven and JDK 17 ----------
FROM maven:3.8.6-eclipse-temurin-17 AS build
# Set working directory inside the container
WORKDIR /app

# Copy only the pom.xml first (used to resolve dependencies)
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the project, skipping tests to save time
RUN mvn clean package -DskipTests


# ---------- STAGE 2: Run with lightweight JDK ----------
FROM eclipse-temurin:17-jdk
# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file from the previous build stage
COPY --from=build /app/target/save-bulgaria-0.0.1-SNAPSHOT.jar app.jar

# Set environment variable for Spring Boot to pick up the port
ENV SERVER_PORT=${PORT:-8080}

# Expose port 8080 so Docker knows which port the app listens on
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
