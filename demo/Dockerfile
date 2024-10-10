# First stage: build the application with Maven
FROM maven:3.8-openjdk-17-slim AS build

# Set the working directory
WORKDIR /app

# Copy the entire project to the container
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Second stage: create the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the first stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
