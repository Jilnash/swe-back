FROM bellsoft/liberica-openjre-alpine:22 AS layers
ADD target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]