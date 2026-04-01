FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY api-gateway api-gateway

RUN chmod +x ./gradlew && sed -i 's/\r$//' gradlew
RUN ./gradlew :api-gateway:clean :api-gateway:bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/api-gateway/build/libs/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]