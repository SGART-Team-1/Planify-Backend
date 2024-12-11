FROM amazoncorretto:21-alpine-jdk

COPY Planify-BE-2024-1.0/target/planify-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]