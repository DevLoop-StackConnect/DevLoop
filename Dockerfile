FROM openjdk:17-jdk-alpine
RUN apt-get update || (apt-get install -y apt && apt-get update) \
    && apt-get install -y telnet \
    && rm -rf /var/lib/apt/lists/*

COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]