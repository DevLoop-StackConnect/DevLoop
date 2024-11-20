FROM ubuntu:focal
RUN apt-get update && apt-get install -y openjdk-17-jdk telnet && rm -rf /var/lib/apt/lists/*

COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]