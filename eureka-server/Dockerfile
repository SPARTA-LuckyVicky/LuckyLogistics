# 1단계 - 빌드
# Gradle + JDK 21 이미지로 JAR 파일 생성
FROM gradle:8.8-jdk21 AS build

WORKDIR /app

# 멀티모듈 빌드에 필요한 루트 설정 파일 우선 복사
# 소스보다 먼저 복사하면 의존성이 바뀌지 않는 한 캐시가 재사용됨
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# eureka-server 서브모듈 소스 복사
COPY eureka-server ./eureka-server

# eureka-server 모듈만 빌드 (테스트 제외)
RUN gradle :eureka-server:bootJar -x test --no-daemon

# 2단계 - 실행
# JDK 전체가 아닌 JRE만 포함한 경량 이미지 사용
FROM eclipse-temurin:21-jre

WORKDIR /app

# 1단계에서 생성된 JAR만 복사
COPY --from=build /app/eureka-server/build/libs/eureka-server.jar app.jar

EXPOSE 19090

ENTRYPOINT ["java", "-jar", "app.jar"]