FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/GotGam-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8111
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]