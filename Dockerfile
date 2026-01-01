# Build stage
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY . .

RUN gradle clean build -x test

# Runtime stage
FROM openjdk:21-jdk-slim

WORKDIR /app

# Create logs directory
RUN mkdir -p logs

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Create non-root user for security
RUN useradd -r -u 1000 -m -c "app user" -d /app -s /bin/false app && \
    chown -R app:app /app

USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=local

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]