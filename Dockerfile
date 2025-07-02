# ===============================
# Build stage
# ===============================

FROM maven:3.8.3-openjdk-21 AS build

WORKDIR /home/app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN mvn clean package

# ===============================
# Package stage
# ===============================

FROM openjdk:21-jdk-slim

# Copy the built JAR from build stage
COPY --from=build /home/app/target/qualitymanagementsystem-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar

# Expose port
EXPOSE 8082

# Run the JAR
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]
