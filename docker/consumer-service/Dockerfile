FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY target/consumer-service-1.0-SNAPSHOT.jar /app/consumer-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/consumer-service.jar"]
