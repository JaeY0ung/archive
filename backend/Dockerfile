# 베이스 이미지로 OpenJDK를 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리를 설정
WORKDIR /app

# 빌드된 JAR 파일과 SSL 인증서 파일을 컨테이너로 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar ./backend.jar
COPY src/main/resources/arc-hive.shop.p12 ./arc-hive.shop.p12 

# JAR 파일 실행 명령
CMD ["java", "-jar", "./backend.jar"]