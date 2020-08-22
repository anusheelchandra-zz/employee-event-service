FROM openjdk:11-jdk-slim

ADD target/employee-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar