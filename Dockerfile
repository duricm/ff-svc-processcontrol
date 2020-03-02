FROM openjdk:8
RUN mvn clean package
COPY ./target/bitcoin-card-api-1.0.jar .
CMD ["java", "-jar", "bitcoin-card-api-1.0.jar"]
