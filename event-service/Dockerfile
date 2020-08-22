FROM openjdk:11-jdk-slim

ADD target/event-service-*.jar app.jar

EXPOSE 8081

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar