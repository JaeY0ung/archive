FROM openjdk:11-jre-slim
WORKDIR /app
COPY build/libs/backend-0.0.1-SNAPSHOT.jar ./backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]
