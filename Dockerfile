# Use Java 21 base image with Maven pre-installed
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy Maven Wrapper files first (for better Docker layer caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Pre-fetch dependencies
RUN ./mvnw dependency:go-offline

# Copy the entire source
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "target/qualitymanagementsystem-0.0.1-SNAPSHOT.jar"]
