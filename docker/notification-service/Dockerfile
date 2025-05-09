FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY notification-service/target/notification-service-1.0-SNAPSHOT.jar /app/notification-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]
