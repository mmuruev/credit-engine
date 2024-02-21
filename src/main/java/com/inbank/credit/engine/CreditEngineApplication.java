package com.inbank.credit.engine;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class CreditEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditEngineApplication.class, args);
	}

}
