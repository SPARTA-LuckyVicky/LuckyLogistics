# 1단계 - 빌드
FROM gradle:8.8-jdk21 AS build
WORKDIR /app

# 루트 설정 파일 복사 (캐시 활용)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# notification-service 소스 복사
COPY notification-service ./notification-service

# notification-service만 빌드 (테스트 제외)
RUN gradle :notification-service:bootJar -x test --no-daemon

# 2단계 - 실행
FROM eclipse-temurin:21-jre
WORKDIR /app

# 비루트 사용자 생성 (보안)
RUN addgroup --system appgroup \
    && adduser --system --ingroup appgroup appuser

# 빌드된 JAR 복사
COPY --from=build /app/notification-service/build/libs/app.jar app.jar

RUN chown appuser:appgroup /app/app.jar

USER appuser

EXPOSE 19700

ENTRYPOINT ["java", "-jar", "app.jar"]