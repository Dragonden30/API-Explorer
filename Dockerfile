FROM openjdk:22-jdk

COPY target/APIexplorer-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "APIexplorer-0.0.1-SNAPSHOT.jar"]