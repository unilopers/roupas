package com.unilopers.roupas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RoupasApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoupasApplication.class, args);
	}

}
