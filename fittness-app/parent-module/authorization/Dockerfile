## Use an official lightweight Java image.
## https://hub.docker.com/_/openjdk
## https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
#FROM maven:3.8.2-openjdk-17-slim AS build
#WORKDIR /app
#
## Copy the pom.xml file and download the dependencies. This will be cached if the pom.xml file doesn't change.
#COPY ./pom.xml ./pom.xml
#RUN mvn dependency:go-offline -B
#
## Copy the rest of the code and build the application.
#COPY ./ ./
#RUN mvn package
#
#FROM openjdk:17-slim
#WORKDIR /app
#COPY --from=build /app/target/fitness-app-authorization.jar /app/fitness-app-authorization.jar
#ENTRYPOINT ["java","-jar","/app/fitness-app-authorization.jar"]

FROM openjdk:17

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]