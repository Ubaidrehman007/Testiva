# ---------- Stage 1 : Build ----------
FROM eclipse-temurin:21-jdk-alpine AS builder

LABEL authors="Ubaidrehman007"

WORKDIR /app

# Copy Maven Wrapper
COPY mvnw .
COPY .mvn .mvn

# Give execute permission
RUN chmod +x mvnw

# Copy pom.xml
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build Spring Boot jar
RUN ./mvnw clean package -DskipTests


# ---------- Stage 2 : Run ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy built jar
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8484

# Run application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]