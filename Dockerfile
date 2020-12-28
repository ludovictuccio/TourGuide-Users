FROM openjdk:11
EXPOSE 9001
ARG JAR_FILE=build/libs/users-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]