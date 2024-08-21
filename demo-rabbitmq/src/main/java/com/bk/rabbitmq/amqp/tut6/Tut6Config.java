package com.bk.rabbitmq.amqp.tut6;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tut6Config {

    private static class ClientConfig {

        @Bean
        public DirectExchange rpcExchangeOnClient() {
            return new DirectExchange("tut.rpc");
        }

    }

    private static class ServerConfig {

        @Bean
        public Queue rpcRequestQueue() {
            return new Queue("tut.rpc.requests");
        }

        @Bean
        public DirectExchange rpcExchangeOnServer() {
            return new DirectExchange("tut.rpc");
        }

        @Bean
        public Binding binding6(DirectExchange rpcExchangeOnServer,
                               Queue rpcRequestQueue) {
            return BindingBuilder.bind(rpcRequestQueue)
                    .to(rpcExchangeOnServer)
                    .with("rpc");
        }

        @Bean
        public Tut6Server server() {
            return new Tut6Server();
        }

    }
}