package com.bk.ms.kafka.producer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class OutboundService {

    private final KafkaTemplate<String, String> template;

    @Transactional(rollbackFor = Exception.class)
    public void publishMessage(String topic, String key, String message) {
        try {
            template.send(topic, key, message);
            log.info("published the message with key: {} to topic: {}", key, topic);
        } catch (Exception e) {
            log.error("Error occurred while publishing message with key: {} to topic: {} , message: {}",
                    key, topic, message);
            throw new KafkaException("Error occurred while publishing message with key: " + key+" to topic: "+topic,e);
        }
    }

}
