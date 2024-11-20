FROM openjdk:17-jdk
RUN apt-get update && apt-get install -y telnet && rm -rf /var/lib/apt/lists/*
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]