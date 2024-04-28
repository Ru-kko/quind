FROM maven:3.9.6-sapmachine-17 AS builder

WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim AS runtime

WORKDIR /app
COPY --from=builder /app/target/ /app

ENV DB_HOST=
ENV DB_PORT=
ENV DB_NAME=
ENV DB_USER=
ENV DB_PASSWORD=
ENV PORT=80

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/app/backend-0.0.1-SNAPSHOT.jar"]