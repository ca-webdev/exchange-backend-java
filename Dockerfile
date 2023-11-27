#Build stage

FROM gradle:latest AS BUILD
WORKDIR /app
COPY . . 
RUN gradle build

# Package stage

FROM openjdk:latest
ENV JAR_NAME=exchange-backend-java-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
EXPOSE 8080
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME  