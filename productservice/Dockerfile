# Etapa 1: Compilar el código (Imagen pesada con JDK)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiamos solo lo necesario para descargar dependencias primero (Aprovecha la caché de Docker)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copiamos el código fuente y compilamos
COPY src src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen final (Imagen ligera con JRE para producción)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Extraemos el .jar compilado de la Etapa 1
COPY --from=build /app/target/productservice-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]