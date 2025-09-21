FROM alpine/java:22-jdk
WORKDIR /app
COPY pom.xml .
RUN apk add maven
RUN mvn package
COPY src ./src
RUN mvn verify
EXPOSE 8080