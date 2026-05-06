# ---------- Stage 1 : Build ----------
FROM eclipse-temurin:17-jdk-alpine AS builder

LABEL authors="Ubaidrehman007"

WORKDIR /app

# Copy Maven wrapper files
COPY mvnw .
COPY .mvn .mvn

# Give permission
RUN chmod +x mvnw

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build jar
RUN ./mvnw clean package -DskipTests


# ---------- Stage 2 : Run ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose application port
EXPOSE 8484

# Run application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
