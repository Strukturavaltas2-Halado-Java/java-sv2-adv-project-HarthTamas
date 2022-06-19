FROM openjdk:17
RUN mkdir /opt/app
COPY target/Berthbooking-0.0.1-SNAPSHOT.jar /opt/app/Berthbooking.jar
CMD ["java","-jar","/opt/app/Berthbooking.jar"]