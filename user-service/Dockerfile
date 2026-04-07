FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY user-service/build.gradle user-service/build.gradle

RUN chmod +x ./gradlew && sed -i 's/\r$//' gradlew

RUN ./gradlew :user-service:dependencies --no-daemon

COPY user-service user-service

RUN chmod +x ./gradlew

RUN ./gradlew :user-service:clean :user-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 보안을 위해 non-root 사용자 생성 및 사용
RUN addgroup -g 1001 appgroup && \
    adduser -u 1001 -G appgroup -D appuser

COPY --from=build /app/user-service/build/libs/*.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]