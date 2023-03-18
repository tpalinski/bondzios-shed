package com.bondzio.bondziosshed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.bondzio.bondziosshed.controllers"})
public class BondziosShedApplication {

	public static void main(String[] args) {
		SpringApplication.run(BondziosShedApplication.class, args);
	}

}
