package com.jg.filedownload;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DownloadController {

    private final SelfApiClient selfApiClient;

    /**
     * Retrieves a file from another microservice as a StreamingResponseBody, which is then propagated as the response
     * of this endpoint.
     * Header Content-Disposition: required to specify the file being attached.
     * Header Content-Length: required to specify the file's size in bytes, without which, progress is not shown by browsers.
     */
    @GetMapping(value = "/bridged/{fileName}")
    public ResponseEntity<StreamingResponseBody> getFileBridged(@PathVariable final String fileName) throws IOException {
        log.debug("Retrieving file [{}] from file-api.", fileName);
        final Response fileResponse = selfApiClient.getFile(fileName);
        final InputStream fileInputStream = fileResponse.body().asInputStream();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Long.parseLong(((List<String>) fileResponse.headers().get(HttpHeaders.CONTENT_LENGTH)).get(0)))
                .body(getStreamingResponseBody(fileInputStream));
    }

    /**
     * Retrieves a File's content by streaming from FileInputStream to StreamingResponseBody as a response.
     * Header Content-Disposition: required to specify the file being attached.
     * Header Content-Length: required to specify the file's size in bytes, without which, progress is not shown by browsers.
     */
    @GetMapping(value = "/{fileName}")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable final String fileName) throws IOException {
        log.debug("Received request to download file [{}].", fileName);
        final File file = new File("files/" + fileName);
        final FileInputStream fileInputStream = new FileInputStream(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(getStreamingResponseBody(fileInputStream));
    }

    private StreamingResponseBody getStreamingResponseBody(final InputStream fileInputStream) {
        log.debug("Generating StreamingResponseBody for transferring InputStream to OutputStream.");
        return outputStream -> {
            fileInputStream.transferTo(outputStream);
            fileInputStream.close();
        };
    }

}
