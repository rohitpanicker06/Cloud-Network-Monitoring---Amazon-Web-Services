FROM openjdk:8
ADD target/VpcFlowLogs-POC-1.0-SNAPSHOT-jar-with-dependencies.jar VpcFlowLogs-POC.jar
#ADD  src/main/resources/config.properties config.properties

ENTRYPOINT ["java", "-jar", "VpcFlowLogs-POC.jar"]