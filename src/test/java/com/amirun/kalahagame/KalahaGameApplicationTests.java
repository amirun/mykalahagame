package com.amirun.kalahagame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class KalahaGameApplicationTests {

	@Bean
	@ServiceConnection
	MongoDBContainer mongoDbContainer() {
		return new MongoDBContainer(DockerImageName.parse("mongo:latest"));
	}

	public static void main(String[] args) {
		SpringApplication.from(KalahaGameApplication::main).with(KalahaGameApplicationTests.class).run(args);
	}

	@DynamicPropertySource
	static void setDynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.main.allow-bean-definition-overriding", () ->"true" );
	}
}
