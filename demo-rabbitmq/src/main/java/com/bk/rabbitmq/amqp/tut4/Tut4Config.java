package com.bk.rabbitmq.amqp.tut4;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tut4Config {

    @Bean
    public DirectExchange direct() {
        return new DirectExchange("tut.direct");
    }


    @Bean
    public Queue autoDeleteQueue3() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue autoDeleteQueue4() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding binding41a(DirectExchange direct,
                             Queue autoDeleteQueue3) {
        return BindingBuilder.bind(autoDeleteQueue3)
                .to(direct)
                .with("orange");
    }

    @Bean
    public Binding binding41b(DirectExchange direct,
                             Queue autoDeleteQueue3) {
        return BindingBuilder.bind(autoDeleteQueue3)
                .to(direct)
                .with("black");
    }

    @Bean
    public Binding binding42a(DirectExchange direct,
                             Queue autoDeleteQueue4) {
        return BindingBuilder.bind(autoDeleteQueue4)
                .to(direct)
                .with("green");
    }

    @Bean
    public Binding binding42b(DirectExchange direct,
                             Queue autoDeleteQueue4) {
        return BindingBuilder.bind(autoDeleteQueue4)
                .to(direct)
                .with("black");
    }

    @Bean
    public Tut4Receiver tut4Receiver() {
        return new Tut4Receiver();
    }

}