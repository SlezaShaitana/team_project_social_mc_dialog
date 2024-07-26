# Первый этап: сборка приложения
FROM gradle:8.8-jdk17-alpine AS build
WORKDIR /home/app
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# Второй этап: создание минимального образа с JRE для запуска приложения
FROM openjdk:17-jdk-alpine

ARG JAR_FILE=/home/app/build/libs/*.jar
COPY --from=build ${JAR_FILE} /app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/app.jar"]
