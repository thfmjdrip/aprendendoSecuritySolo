# Multi-stage Dockerfile for Project Security API
# Build: docker build -t project-security:latest .
# Run: docker run -p 8089:8089 project-security:latest

# ============================================================
# STAGE 1: Build - Maven + JDK 21
# ============================================================
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Cache dependencies layer (leverage Docker layer caching)
COPY pom.xml .
RUN mvn -B dependency:go-offline -DskipTests

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests package

# ============================================================
# STAGE 2: Runtime - JRE 21 Alpine (minimal)
# ============================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Metadata labels (OCI standard)
LABEL org.opencontainers.image.title="Project Security API" \
      org.opencontainers.image.description="Spring Boot 4 REST API with RSA-signed JWT, RBAC, PostgreSQL" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.authors="Your Name <your.email@example.com>" \
      org.opencontainers.image.source="https://github.com/your-username/project.security" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.base.name="eclipse-temurin:21-jre-alpine"

# Install runtime dependencies
# - dumb-init: proper signal handling (PID 1)
# - wget: healthcheck
# - curl: debugging
RUN apk add --no-cache \
    dumb-init \
    wget \
    curl \
    && rm -rf /var/cache/apk/*

# Create non-root user (security best practice)
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup -h /app -s /sbin/nologin

# Set working directory
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Verify JAR exists and is readable
RUN ls -la app.jar && jar tf app.jar | head -20

# Create directory for any runtime files (logs, temp)
RUN mkdir -p /app/logs /app/tmp && \
    chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8089

# Healthcheck (requires spring-boot-starter-actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
    CMD wget -q --spider http://localhost:8089/actuator/health || exit 1

# JVM Options for containers
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=UTC"

# Entry point with dumb-init for proper signal handling
ENTRYPOINT ["dumb-init", "--"]

# Default command
CMD ["java", "-jar", "app.jar"]

# ============================================================
# ALTERNATIVE: Distroless base (uncomment for production)
# ============================================================
# FROM gcr.io/distroless/java21-debian12:nonroot AS distroless
#
# COPY --from=build /app/target/*.jar /app.jar
#
# USER nonroot:nonroot
# EXPOSE 8089
# ENTRYPOINT ["java", "-jar", "/app.jar"]