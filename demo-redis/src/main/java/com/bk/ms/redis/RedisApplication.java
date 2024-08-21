package com.bk.ms.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class RedisApplication {

	@Autowired
	Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);
	}


	@PostConstruct
	void initialize() {
		var port =environment.getProperty("local.server.port");
		log.info("Server listening for requests on port: {}", port);
//		log.info("Application started");

	}

}
