package com.bk.rabbitmq.amqp.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {

    @Bean("helloQueue")
    public Queue helloQueue() {
        return new Queue("helloQueue");
    }

    @Bean("workQueue")
    public Queue workQueue() {
        return new Queue("workQueue");
    }


}