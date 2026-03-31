# Build stage
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY hub-service/build.gradle hub-service/build.gradle
COPY hub-service/src hub-service/src

RUN chmod +x gradlew && ./gradlew :hub-service:bootJar -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/hub-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]