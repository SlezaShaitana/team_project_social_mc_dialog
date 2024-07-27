# Первый этап: сборка приложения
FROM maven:3.9.7-amazoncorretto-17 AS build
WORKDIR /home/app
COPY pom.xml .mvn ./
COPY src ./src
RUN mvn package -DskipTests


# Второй этап: создание минимального образа с JRE для запуска приложения
FROM openjdk:17-jdk-alpine

ARG JAR_FILE=/home/app/target/*.jar
COPY --from=build ${JAR_FILE} /app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/app.jar"]
