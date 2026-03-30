# Build stage
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY company-service/build.gradle company-service/build.gradle
COPY company-service/src company-service/src

# Windows CRLF → Linux LF 변환 후 실행권한 부여
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew :company-service:bootJar -x test --no-daemon
# Run stage
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --gid 1001 appuser

COPY --from=builder /app/company-service/build/libs/*.jar app.jar

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]