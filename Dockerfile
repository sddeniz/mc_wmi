# Pull base image.
FROM java:8
ADD ./target/mc_wmi-0.0.1-SNAPSHOT.jar mc_wmi-0.0.1-SNAPSHOT.jar

EXPOSE 8060
CMD java -jar mc_wmi-0.0.1-SNAPSHOT.jar