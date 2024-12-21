FROM openjdk:19
COPY /build/libs/raft-server.jar raft-server.jar
ENTRYPOINT ["java",  "-jar","/raft-server.jar"]
