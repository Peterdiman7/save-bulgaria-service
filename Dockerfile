# Stage 1: Build with Maven & Java
FROM maven:3.8.6-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the app with lightweight JRE
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/save-bulgaria-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
