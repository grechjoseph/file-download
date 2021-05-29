package com.jg.filedownload;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DownloadController {

    private final SelfApiClient selfApiClient;

    @GetMapping(value = "/bridged/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getFileBridged(@PathVariable final String fileName,
                                                final HttpServletResponse response) throws FileNotFoundException {
        log.debug("getFileBridged start.");
        final Response fileResponse = selfApiClient.getFile(fileName);
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];

            while ((nRead = fileResponse.body().asInputStream().read(data, 0, data.length)) != -1) {
                log.debug("getFileBridged write...");
                outputStream.write(data, 0, nRead);
            }
        };
    }

    @GetMapping(value = "/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getFile(@PathVariable final String fileName,
                                         final HttpServletResponse response) throws FileNotFoundException {
        log.debug("getFile start.");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        final InputStream inputStream = new FileInputStream("files/" + fileName);
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                log.debug("getFile write...");
                outputStream.write(data, 0, nRead);
            }
        };
    }

}
