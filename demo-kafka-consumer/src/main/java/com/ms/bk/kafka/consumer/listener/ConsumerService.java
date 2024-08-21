package com.ms.bk.kafka.consumer.listener;

import com.bk.ms.kafka.models.Event;
import com.bk.ms.kafka.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics="#{'${app.kafka.topics.inbound.example}'}",
                   groupId = "#{'${spring.application.name}'}")
    @Transactional
    public void consume(ConsumerRecord<String, String> cr) throws JsonProcessingException {
        log.info("received message on topic: {}, partition: {}  with key: {}, value: {}",
                cr.topic(), cr.partition(), cr.key(), cr.value());
        try {
            var user = objectMapper.readValue(cr.value(), User.class);
            log.info("reconstructed user instance: {}", user);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing the request", e);
        }
    }

    @KafkaListener(topics="#{'${app.kafka.topics.inbound.events}'}", groupId = "#{'${spring.application.name}'}")
    @Transactional
    public void consumeEvents(ConsumerRecord<String, String> cr) throws JsonProcessingException {
        log.info("received event on topic: {}, partition: {}  with key: {}, value: {}",
                cr.topic(), cr.partition(), cr.key(), cr.value());
        try {
            var event = objectMapper.readValue(cr.value(), Event.class);
            log.info("event: {}", event);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing the request", e);
        }
    }
}
