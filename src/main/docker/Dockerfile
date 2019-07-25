FROM openjdk:8-jdk
RUN useradd -ms /bin/bash lapps
COPY *.jar /home/lapps
USER lapps
WORKDIR /home/lapps
CMD ["java", "-Xmx2G","-jar","delimited.jar"]
