FROM openjdk:11-jdk-slim

WORKDIR /app

COPY build/libs/comand-hub.jar app.jar
COPY keycloak.json /app/keycloak.json

EXPOSE 8090

CMD ["java", "-jar", "app.jar"]
