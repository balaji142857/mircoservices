package com.bk.ms.kafka.producer.service;

import com.bk.ms.kafka.models.Event;
import com.bk.ms.kafka.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessorService {

    @Value("${app.kafka.topics.outbound.example}")
    private String exampleTopic;
    @Value("${app.kafka.topics.outbound.events}")
    private String eventsTopic;

    private final ObjectMapper objectMapper;

    private final OutboundService outboundService;

    @Transactional
    public String process(String key, User user) {
        log.info("Received request for key: {}, user: {}", key, user);
        try {
            if (Objects.isNull(user) || !StringUtils.hasText(key)) {
                log.warn("Received invalid request key: {}, user: {}", key, user);
                return "NOK";
            }
            var json = objectMapper.writeValueAsString(user);
            log.info("Message in json format: {}", json);
            outboundService.publishMessage(exampleTopic, key, json);

            return "OK";
        } catch (Exception e) {
            log.error("Error occurred while processing the request", e);
            return "NOK";
        }
    }


    @Transactional
    public String process(Event event) {
        log.info("Received request for event: {}",event);
        try {
            if (Objects.isNull(event) || Objects.isNull(event.eventType())) {
                return "NOK";
            }
            var json = objectMapper.writeValueAsString(event);
            log.info("Message in json format: {}", json);
            outboundService.publishMessage(eventsTopic, event.eventType().name(), json);

            return "OK";
        } catch (Exception e) {
            log.error("Error occurred while processing the request", e);
            return "NOK";
        }
    }
}
