FROM openjdk:17-jdk-alpine
RUN apk add --no-cache busybox-extras

COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]