FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y telnet

COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]