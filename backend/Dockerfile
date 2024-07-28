# OpenJDK 17 이미지 사용
FROM openjdk:17-jre-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar ./backend.jar

# JAR 파일 실행 명령
ENTRYPOINT ["java", "-jar", "backend.jar"]
