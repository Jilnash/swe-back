# Use an official OpenJDK runtime as a parent image
FROM bellsoft/liberica-openjdk-alpine:22

# Set the working directory in the container
WORKDIR /app

# Copy the project's pom.xml and source code to the container
COPY pom.xml /app
COPY src /app/src
COPY mvnw /app
COPY .mvn /app/.mvn

# Package the application using Maven
RUN ./mvnw package

# Copy the packaged JAR file to the container
COPY target/SWE-CSCI361-0.0.1-SNAPSHOT.jar /app/SWE-CSCI361.jar

# Expose port 8080
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/SWE-CSCI361.jar"]

#docker build -t swe .
#docker run -p 8080:8080 swe