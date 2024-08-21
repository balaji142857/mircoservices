package com.bk.rabbitmq.amqp.tut4;

import com.bk.rabbitmq.amqp.api.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Component
public class Tut4Sender implements Sender {

    private final RabbitTemplate template;

    private final DirectExchange direct;

    AtomicInteger index = new AtomicInteger(0);

    AtomicInteger count = new AtomicInteger(0);

    private final String[] keys = {"orange", "black", "green"};

    public void send(String message) {
        var index = this.index.get();
        String key = keys[index];
        log.info("Received request for sending message: {}, key: {}, index: {}",
                message, key, index);
        template.convertAndSend(direct.getName(), key, message);
        log.info(" [x] Sent {}",message);
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        StringBuilder builder = new StringBuilder("Hello to ");
        if (this.index.incrementAndGet() == 3) {
            this.index.set(0);
        }
        String key = keys[this.index.get()];
        builder.append(key).append(' ');
        builder.append(this.count.get());
        String message = builder.toString();
        template.convertAndSend(direct.getName(), key, message);
        log.info(" [x] Sent {}",message);
    }

}