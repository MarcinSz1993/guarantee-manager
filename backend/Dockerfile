FROM maven:3.9.6-eclipse-temurin-21 AS build
LABEL authors="MarcinSz1993"
COPY . .
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

FROM openjdk:21-jdk-slim
COPY --from=build /target/*.jar /app/guarantee-manager.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app/guarantee-manager.jar"]