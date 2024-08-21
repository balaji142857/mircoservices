package com.ms.bk.kafka.consumer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.lang.instrument.UnmodifiableClassException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ErrorHandlerConfig {

    private final KafkaProperties kafkaProperties;

    @Value("${app.kafka.retry.initialInterval}")
    private int initialInterval;

    @Value("${app.kafka.retry.multiplier}")
    private int multiplier;

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer dltRecoverer) {
        BackOff fixedBackOff = new ExponentialBackOff(initialInterval, multiplier);
        var errHandler = new  DefaultErrorHandler(dltRecoverer,fixedBackOff);
        // TODO list of retryable and non-retryable exceptions
        errHandler.addRetryableExceptions(RuntimeException.class);
        errHandler.addNotRetryableExceptions(UnmodifiableClassException.class);
        return errHandler;
    }

    @Bean
    public DeadLetterPublishingRecoverer dltRecoverer(KafkaTemplate<String, byte[]> byteArrTemplate,
                                                      KafkaTemplate<String, String> stringTemplate) {
        Map<Class<?>, KafkaOperations<? extends Object, ? extends Object>> templates = new LinkedHashMap<>();
        templates.put(byte[].class, byteArrTemplate);
        templates.put(String.class, stringTemplate);
        var recoverer = new DeadLetterPublishingRecoverer(templates);
        log.info("Constructed dlt recoverer with templates: {}", templates);
        return recoverer;
    }


    @Bean
    public KafkaTemplate<String, byte[]> byteArrTemplate(ProducerFactory<String, byte[]> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, byte[]> byteArrProducerFactory() {
        Map<String,Object> producerConfigs = kafkaProperties.buildProducerProperties(null);
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }


    @Bean
    public KafkaTemplate<String, String> stringTemplate(ProducerFactory<String, String> stringProducerFactory) {
        return new KafkaTemplate<>(stringProducerFactory);
    }

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String,Object> producerConfigs = kafkaProperties.buildProducerProperties(null);
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }
}
