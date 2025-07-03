# Stage 1: Build the app using Maven + JDK 17
FROM maven:3.8.6-openjdk-17 AS build

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the jar without tests
RUN mvn clean package -DskipTests

# Stage 2: Run the app with a lightweight OpenJDK runtime
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/save-bulgaria-0.0.1-SNAPSHOT.jar app.jar

# Tell Spring Boot to use the PORT environment variable from Render
ENV SERVER_PORT=${PORT:-8080}
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
