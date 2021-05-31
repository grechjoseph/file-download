FROM openjdk

ENV PORT 8080
ENV CLASSPATH /opt/lib

COPY target/file-download-0.0.1-SNAPSHOT.jar /file-download.jar

CMD ["sh", "-c", "java $APPLICATION_ARGS -jar /file-download.jar"]