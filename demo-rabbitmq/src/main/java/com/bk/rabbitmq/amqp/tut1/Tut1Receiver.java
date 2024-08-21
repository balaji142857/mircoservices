package com.bk.rabbitmq.amqp.tut1;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@RabbitListener(queues = "helloQueue")
@Component
@Slf4j
public class Tut1Receiver {

    @RabbitHandler
    public void receive(String in) {
        log.info("Received message: {}", in);
    }
}
