#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/bitcoin-card-api-1.0.jar /usr/local/lib/bitcoin-card-api-1.0.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]

