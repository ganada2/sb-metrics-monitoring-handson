package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class YesOrNoConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(YesOrNoConsumerApplication.class, args);

		// for kill the app Process (강제로 서버 중지를 위해 pid 포함된 파일 생성)
		SpringApplicationBuilder app = new SpringApplicationBuilder(YesOrNoConsumerApplication.class)
				.web(WebApplicationType.NONE);
		app.build().addListeners(new ApplicationPidFileWriter("./yes-or-no-consumer/shutdown.pid"));
		app.run();
	}
}
