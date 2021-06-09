package com.jg.filedownload.controller;

import com.jg.filedownload.feign.SelfApiClient;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.*;

@Slf4j
@RestController
@RequestMapping("/webflux")
@RequiredArgsConstructor
public class WebfluxDownloadController {

    private final SelfApiClient selfApiClient;

    /**
     * Retrieves a file from another microservice as a Mono<Resource>, which is then propagated as the response
     * of this endpoint.
     * Header Content-Disposition: required to specify the file being attached.
     * Header Content-Length: required to specify the file's size in bytes, without which, progress is not shown by browsers.
     */
    @GetMapping(value = "/bridged/{fileName}")
    public ResponseEntity<Mono<Resource>> getFileBridged(@PathVariable final String fileName) throws IOException {
        log.debug("Retrieving file [{}] from file-api.", fileName);
        final Response response = selfApiClient.getFileUsingWebflux(fileName);
        final InputStream inputStream = response.body().asInputStream();
        final InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Long.parseLong(response.headers().get(HttpHeaders.CONTENT_LENGTH).stream().findFirst().orElse("-1")))
                .body(Mono.just(inputStreamResource));
    }

    /**
     * Retrieves a File's content by streaming from FileInputStream to Mono<Resource> as a response.
     * Header Content-Disposition: required to specify the file being attached.
     * Header Content-Length: required to specify the file's size in bytes, without which, progress is not shown by browsers.
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<Mono<Resource>> downloadByWriteWith(@PathVariable final String fileName) throws FileNotFoundException {
        log.debug("Received request to download file [{}].", fileName);
        final File file = new File("files/" + fileName);
        final InputStream inputStream = new FileInputStream(file);
        final InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .contentLength(file.length())
                .body(Mono.just(inputStreamResource));
    }

}
