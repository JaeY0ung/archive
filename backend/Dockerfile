# Step 1: Use a Gradle image to build the project
FROM gradle:7.6.0-jdk17 AS builder

# Copy project files to the build container
COPY --chown=gradle:gradle . /home/gradle/project

# Set the working directory
WORKDIR /home/gradle/project

# Build the project
RUN ./gradlew clean build

# Step 2: Use a slim Java image to create the final image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build container
COPY --from=builder /home/gradle/project/build/libs/backend-0.0.1-SNAPSHOT.jar ./backend.jar

# Run the application
CMD ["java", "-jar", "./backend.jar"]
