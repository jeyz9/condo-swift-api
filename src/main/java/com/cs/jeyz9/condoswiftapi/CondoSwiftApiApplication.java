package com.cs.jeyz9.condoswiftapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CondoSwiftApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CondoSwiftApiApplication.class, args);
	}

}
