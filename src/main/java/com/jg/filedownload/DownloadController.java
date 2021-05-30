package com.jg.filedownload;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
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
@EnableAsync
@RestController
@RequiredArgsConstructor
public class DownloadController {

    private final SelfApiClient selfApiClient;

    @GetMapping(value = "/bridged/{fileName}")
    public ResponseEntity<StreamingResponseBody> getFileBridged(@PathVariable final String fileName) throws IOException {
        log.debug("getFileBridged start.");
        final Response fileResponse = selfApiClient.getFile(fileName);
        final InputStream fileInputStream = fileResponse.body().asInputStream();

        final StreamingResponseBody streamingResponseBody = outputStream -> {
            int nRead;
            byte[] data = new byte[1024];

            while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
                log.trace("getFileBridged write...");
                outputStream.write(data, 0, nRead);
            }

            fileInputStream.close();
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Long.parseLong(((List<String>) fileResponse.headers().get(HttpHeaders.CONTENT_LENGTH)).get(0)))
                .body(streamingResponseBody);
    }

    @GetMapping(value = "/{fileName}")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable final String fileName) throws IOException {
        log.debug("getFile start.");
        final File file = new File("files/" + fileName);
        final FileInputStream fileInputStream = new FileInputStream(file);

        final StreamingResponseBody streamingResponseBody = outputStream -> {
            int nRead;
            byte[] data = new byte[1024];

            while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
                log.trace("getFileBridged write...");
                outputStream.write(data, 0, nRead);
            }

            fileInputStream.close();
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(streamingResponseBody);
    }

}
