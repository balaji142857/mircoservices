package com.bk.rabbitmq.amqp.tut2;

import com.bk.rabbitmq.amqp.api.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Tut2Sender implements Sender {

    private final RabbitTemplate template;

    @Qualifier("workQueue")
    private final Queue queue;

    public void send(String message) {
        log.info("Sending message: {} to the queue: {}", message, queue.getName());
        this.template.convertAndSend(queue.getName(), message);
        log.info("Sent {}",message);
    }
}

