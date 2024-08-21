package com.bk.rabbitmq.amqp.tut2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tut2Config {

    @Bean
    public Tut2Receiver receiver1() {
        return new Tut2Receiver(1);
    }

    @Bean
    public Tut2Receiver receiver2() {
        return new Tut2Receiver(2);
    }
}
