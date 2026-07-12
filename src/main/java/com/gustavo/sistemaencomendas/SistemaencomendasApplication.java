package com.gustavo.sistemaencomendas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaencomendasApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaencomendasApplication.class, args);
	}
}