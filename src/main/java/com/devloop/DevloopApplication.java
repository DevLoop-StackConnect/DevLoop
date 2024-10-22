package com.devloop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DevloopApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevloopApplication.class, args);
	}

}
