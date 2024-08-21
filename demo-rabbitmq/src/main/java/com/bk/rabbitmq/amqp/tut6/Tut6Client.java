package com.bk.rabbitmq.amqp.tut6;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class Tut6Client {

    private final RabbitTemplate template;

    private final DirectExchange rpcExchangeOnClient;

    int start = 0;

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        log.info(" [x] Requesting fib({})",start);
        Integer response = (Integer) template.convertSendAndReceive(rpcExchangeOnClient.getName(), "rpc", start++);
        log.info(" [.] Got {}",response);
    }
}