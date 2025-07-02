# ===============================
# Build stage
# ===============================

FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /home/app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# ===============================
# Package stage
# ===============================

FROM eclipse-temurin:21-jdk-jammy

# Copy the built JAR from build stage
COPY --from=build /home/app/target/qualitymanagementsystem-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar

# Expose port
EXPOSE 8082

# Run the JAR
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]
