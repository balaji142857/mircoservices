package com.bk.rabbitmq.amqp.api;

@FunctionalInterface
public interface Sender {

    void send(String message);
}
