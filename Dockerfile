FROM openjdk:17-jdk-alpine
RUN apk update && apk add --no-cache inetutils

COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]