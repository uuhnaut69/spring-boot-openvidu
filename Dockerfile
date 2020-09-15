FROM openjdk:11.0.8
LABEL maintainer="@uuhnaut69"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
