package com.jg.filedownload;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to simulate fetching a StreamingResponseBody from another microservice (even though this Feign client
 * point to this same microservice).
 */
@FeignClient(name = "self-api-client", url = "${feign.file-api.url:http://localhost:8080}")
public interface SelfApiClient {

    @GetMapping(value = "/{fileName}")
    feign.Response getFile(@PathVariable final String fileName);

}
