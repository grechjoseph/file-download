package com.jg.filedownload;

import com.jg.filedownload.feign.SelfApiClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {
		SelfApiClient.class
})
public class FileDownloadApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileDownloadApplication.class, args);
	}

}
