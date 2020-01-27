FROM java:8
VOLUME /tmp
ADD build/libs/demo-0.0.1-SNAPSHOT.jar demo-0.0.1-SNAPSHOT.jar
RUN bash -c 'touch /demo-0.0.1-SNAPSHOT.jar'
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/demo-0.0.1-SNAPSHOT.jar"]