FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY user-service/build.gradle user-service/build.gradle

RUN chmod +x ./gradlew && sed -i 's/\r$//' gradlew

# 4. 의존성 갱신
RUN ./gradlew :user-service:dependencies --no-daemon

COPY user-service user-service
RUN ./gradlew :user-service:clean :user-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/user-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]