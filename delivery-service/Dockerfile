# build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY delivery-service/build.gradle delivery-service/build.gradle
COPY delivery-service/src delivery-service/src

RUN gradle :delivery-service:bootJar --no-daemon

# RUN
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/delivery-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
