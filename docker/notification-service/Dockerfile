FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY notification-service/target/notification-service-1.0-SNAPSHOT.jar /app/notification-service.jar
COPY scripts/wait-for-postgres.sh /app/wait-for-postgres.sh

RUN chmod +x /app/wait-for-postgres.sh

EXPOSE 8081
ENTRYPOINT ["/app/wait-for-postgres.sh"]
