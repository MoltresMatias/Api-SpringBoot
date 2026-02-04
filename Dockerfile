# Etapa 1: Construcción (Builder)
# Usamos una imagen con JDK 21 para compilar el proyecto
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copiamos todo el código fuente al contenedor
COPY . .

# Damos permisos de ejecución al wrapper y compilamos (saltando tests para agilizar el build)
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
# Usamos una imagen ligera con solo JRE 21 para ejecutar
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos solo el JAR construido desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]