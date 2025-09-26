FROM alpine/java:22-jdk
WORKDIR /app

COPY pom.xml .
RUN apk add --no-cache maven
RUN mvn package

COPY src ./src
RUN mvn verify

EXPOSE 8199
CMD ["java", "src/main/java/CurlForwarder.java"]