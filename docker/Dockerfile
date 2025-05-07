FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY transaction-service/target/transaction-service*.jar /app/transaction-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/transaction-service.jar"]

COPY notification-service/target/notification-service*.jar /app/notification-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]
