FROM alpine/java:22-jdk
WORKDIR /app

COPY src/main/java/Service1.java .
RUN javac Service1.java

VOLUME /vstorage
EXPOSE 8199
CMD ["java", "Service1"]