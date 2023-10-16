package com.amirun.kalahagame;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Kalaha Game", version = "1.0", description = "API to create and play the kalaha game."))
public class KalahaGameApplication {
	public static void main(String[] args) {
		SpringApplication.run(KalahaGameApplication.class, args);
	}
}
