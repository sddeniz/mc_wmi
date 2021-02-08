# Pull base image.
FROM java:8
ADD ./target/Edge-1.0.0.jar Edge-1.0.0.jar


EXPOSE 8049
CMD java -jar Edge-1.0.0.jar