FROM java:8

LABEL maintainer="woung717@gmail.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/rest-api-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} rest-api.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/rest-api.jar"]
