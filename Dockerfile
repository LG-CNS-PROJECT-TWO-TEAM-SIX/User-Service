FROM amazoncorretto:17
MAINTAINER dev@welab.com
VOLUME /tmp
EXPOSE 8080
COPY build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

#FROM gradle:7.6-jdk17 AS builder
#COPY . /app
#WORKDIR /app
#RUN gradle clean build
#
#FROM gcr.io/distroless/java17
#COPY --from=builder /app/build/libs/*.jar /app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]