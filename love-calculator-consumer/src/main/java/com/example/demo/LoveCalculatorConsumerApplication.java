package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class LoveCalculatorConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoveCalculatorConsumerApplication.class, args);

		// for kill the app Process (강제로 서버 중지를 위해 pid 포함된 파일 생성)
		SpringApplicationBuilder app = new SpringApplicationBuilder(LoveCalculatorConsumerApplication.class)
				.web(WebApplicationType.NONE);
		app.build().addListeners(new ApplicationPidFileWriter("./love-calculator-consumer/shutdown.pid"));
		app.run();
	}
}
