# Use an official JDK as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file to the container
COPY ./target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
