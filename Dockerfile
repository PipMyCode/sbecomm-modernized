# Stage 1: Build stage using Java 26
FROM eclipse-temurin:26-jdk-alpine AS build
WORKDIR /app

# Copy the maven wrapper and pom file
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Give execute permission to maven wrapper
RUN chmod +x mvnw

# Download dependencies (this layer is cached)
RUN ./mvnw dependency:go-offline

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the runtime image using Java 26
FROM eclipse-temurin:26-jre-alpine
WORKDIR /app

# Create a non-root user and group for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership of the jar to the non-root user
RUN chown spring:spring app.jar

# Switch to the non-root user
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]