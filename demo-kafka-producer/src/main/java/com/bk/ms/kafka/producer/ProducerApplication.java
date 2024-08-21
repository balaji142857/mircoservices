package com.bk.ms.kafka.producer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class ProducerApplication {

	@Value("${app.mock.produceDummyEvents}") String val;

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

	@PostConstruct
	void init() {
		log.info("Producing mock events: {}", val);
	}

}
