# Use Amazon Corretto 21 as the base image (matches your Java version)
FROM amazoncorretto:21-alpine-jdk

# Create a volume for temporary files (optional but good practice for Spring Boot)
VOLUME /tmp

# Set the working directory
WORKDIR /app

# Copy the built jar file
# Note: This assumes you run 'mvn clean package' before building the docker image
# The jar name matches the artifactId and version in your pom.xml
COPY target/site-0.0.1-SNAPSHOT.jar app.jar

# Explicitly expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
