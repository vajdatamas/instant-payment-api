#!/bin/sh

echo "Waiting for PostgreSQL to be available at $DB_HOST:$DB_PORT..."

while ! nc -z "$DB_HOST" "$DB_PORT"; do
  sleep 1
done

echo "PostgreSQL is up - starting application..."
exec java -jar /app/transaction-service.jar
