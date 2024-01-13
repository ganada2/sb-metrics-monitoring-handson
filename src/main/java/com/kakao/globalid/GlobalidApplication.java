package com.kakao.globalid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication 
public class GlobalidApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlobalidApplication.class, args);
	}

}
