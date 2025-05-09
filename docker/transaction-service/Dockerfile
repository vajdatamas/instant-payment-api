FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY transaction-service/target/transaction-service-1.0-SNAPSHOT.jar /app/transaction-service.jar
COPY ../../scripts/wait-for-postgres.sh /app/wait-for-postgres.sh

RUN chmod +x /app/wait-for-postgres.sh

EXPOSE 8080
ENTRYPOINT ["/app/wait-for-postgres.sh"]
