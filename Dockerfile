# Pull base image.
FROM openjdk:8-jre-alpine
ADD ./target/Edge-1.0.1.jar Edge-1.0.1.jar
EXPOSE 8049
CMD java -jar Edge-1.0.1.jar