import os

services = ['apigateway', 'eurekaserver', 'kafkaservice', 'ordenservice', 'pagoservice', 'productservice']

for s in services:
    path = f"{s}/Dockerfile"
    try:
        with open(path, 'r') as f:
            content = f.read()
        
        # Make replacements
        content = content.replace("FROM eclipse-temurin:21-jdk-jammy AS build", "FROM maven:3.9-eclipse-temurin-21-jammy AS build")
        content = content.replace("COPY mvnw .", "")
        content = content.replace("COPY .mvn .mvn", "")
        content = content.replace("RUN chmod +x mvnw && ./mvnw", "RUN mvn")
        content = content.replace("RUN ./mvnw", "RUN mvn")
        
        with open(path, 'w') as f:
            f.write(content)
        print(f"Patched {path}")
    except Exception as e:
        print(f"Error on {s}: {e}")
