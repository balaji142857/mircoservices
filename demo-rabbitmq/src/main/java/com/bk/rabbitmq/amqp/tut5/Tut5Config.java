package com.bk.rabbitmq.amqp.tut5;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tut5Config {

    @Bean
    public TopicExchange topic() {
        return new TopicExchange("tut.topic");
    }

    private static class ReceiverConfig {

        @Bean
        public Tut5Receiver tut5Receiver() {
            return new Tut5Receiver();
        }

        @Bean
        public Queue autoDeleteQueue5() {
            return new AnonymousQueue();
        }

        @Bean
        public Queue autoDeleteQueue6() {
            return new AnonymousQueue();
        }

        @Bean
        public Binding binding51a(TopicExchange topic,
                                 Queue autoDeleteQueue5) {
            return BindingBuilder.bind(autoDeleteQueue5)
                    .to(topic)
                    .with("*.orange.*");
        }

        @Bean
        public Binding binding51b(TopicExchange topic,
                                 Queue autoDeleteQueue5) {
            return BindingBuilder.bind(autoDeleteQueue5)
                    .to(topic)
                    .with("*.*.rabbit");
        }

        @Bean
        public Binding binding52a(TopicExchange topic,
                                 Queue autoDeleteQueue6) {
            return BindingBuilder.bind(autoDeleteQueue6)
                    .to(topic)
                    .with("lazy.#");
        }

    }


}