# 1. 빌드 스테이지
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 빌드에 필요한 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY delivery-service/build.gradle delivery-service/build.gradle
COPY delivery-service/src delivery-service/src

# 권한 부여 및 빌드 (gradle 대신 ./gradlew 사용)
RUN chmod +x gradlew
RUN ./gradlew :delivery-service:bootJar --no-daemon

# 2. 실행 스테이지
FROM eclipse-temurin:21-jre
WORKDIR /app

# --from=builder 경로를 /app으로 수정!
COPY --from=builder /app/delivery-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]