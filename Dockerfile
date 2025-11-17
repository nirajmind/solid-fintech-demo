# 1. Start with a base Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# 2. Set a working directory inside the container
WORKDIR /app

# 3. Copy the built JAR file from your target folder into the container
# (We assume the Jenkins pipeline will build the jar first)
COPY target/*.jar app.jar

# 4. Expose port 8080 (where Spring Boot runs)
EXPOSE 8080

# 5. Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]