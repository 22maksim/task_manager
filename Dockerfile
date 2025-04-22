FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/task_management-*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
