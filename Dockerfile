FROM openjdk:17-jdk-slim

WORKDIR /user-service

COPY target/user_service-0.0.1-SNAPSHOT.jar user-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "user-service.jar"]