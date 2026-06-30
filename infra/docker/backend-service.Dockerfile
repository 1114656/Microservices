FROM eclipse-temurin:17-jre

WORKDIR /app

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""

EXPOSE 48080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
