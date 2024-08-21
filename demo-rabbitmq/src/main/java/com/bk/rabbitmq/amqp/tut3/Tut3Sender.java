package com.bk.rabbitmq.amqp.tut3;


import com.bk.rabbitmq.amqp.api.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class Tut3Sender implements Sender {

    private final RabbitTemplate template;

    private final FanoutExchange fanout;

    AtomicInteger dots = new AtomicInteger(0);

    AtomicInteger count = new AtomicInteger(0);

    public void send(String message) {
        template.convertAndSend(fanout.getName(), "", message);
        log.info("[x] Sent {}",message);
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        StringBuilder builder = new StringBuilder("Hello");
        if (dots.getAndIncrement() == 3) {
            dots.set(1);
        }
        IntStream.range(0, dots.get()).forEach(i -> builder.append('.'));
        builder.append(count.incrementAndGet());
        String message = builder.toString();
        template.convertAndSend(fanout.getName(), "", message);
        log.info("[x] Sent {}",message);
    }

}