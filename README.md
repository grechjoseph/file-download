<h1>file-download</h1>
This POC demonstrates the capability of allowing large file downloads without the service having to load the whole file into memory.

Instead, the FileInputStream hooked to the resource is buffered-read into a StreamingResponseBody.

<b>Running</b>
1. Add files under ./files directory.
2. Run: <b>mvn clean install -DskipTests && docker build -t test/file-download . && docker-compose up</b>.
3. Test the applicating by opening <b>localhost:8080/file-name.ext</b>.