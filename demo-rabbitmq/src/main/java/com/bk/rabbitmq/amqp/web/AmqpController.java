package com.bk.rabbitmq.amqp.web;

import com.bk.rabbitmq.amqp.api.Sender;
import com.bk.rabbitmq.amqp.tut1.Tut1Sender;
import com.bk.rabbitmq.amqp.tut2.Tut2Sender;
import com.bk.rabbitmq.amqp.tut3.Tut3Sender;
import com.bk.rabbitmq.amqp.tut4.Tut4Sender;
import com.bk.rabbitmq.amqp.tut5.Tut5Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

@Slf4j
@RestController
@RequestMapping("/amqp")
public class AmqpController {
    private final Map<String, Sender> senderMap;

    public AmqpController(Tut1Sender hwSender,
                          Tut2Sender wqSender,
                          Tut3Sender pubSubSender,
                          Tut4Sender directKeyRoutingSender,
                          Tut5Sender topicSender) {
                senderMap = Map.ofEntries(
                entry("tut1", hwSender),
                entry("tut2", wqSender),
                entry("tut3", pubSubSender),
                entry("tut4", directKeyRoutingSender),
                entry("tut5", topicSender));
    }


    @GetMapping("/{sender}/{message}")
    String send(@PathVariable String sender, @PathVariable String message) {
        log.info("Received request to send message for {} {}", sender, message);
        var msgSender  = senderMap.get(sender);
        if (Objects.isNull(msgSender)) {
            return "NOK";
        }
        msgSender.send(message);
        return "OK";
    }

}
