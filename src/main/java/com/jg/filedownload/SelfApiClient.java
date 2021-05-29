package com.jg.filedownload;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "self-api-client", url = "http://localhost:${server.port}")
public interface SelfApiClient {

    @GetMapping(value = "/{fileName}")
    feign.Response getFile(@PathVariable final String fileName);

}
