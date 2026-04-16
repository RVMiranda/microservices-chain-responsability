# syntax=docker/dockerfile:1
# Etapa 1: compilar
FROM maven:3.9-eclipse-temurin-21-jammy AS build
WORKDIR /app

COPY pom.xml .

RUN --network=host mvn dependency:go-offline -B

COPY src src
RUN --network=host mvn clean package -DskipTests

# Etapa 2: imagen final
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/ordenservice-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
