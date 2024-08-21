package com.ms.bk.kafka.consumer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Properties;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class StreamConfig {

    private final KafkaProperties kafkaProperties;

    @Value("${app.kafka.topics.streams.input}") String inputTopic;
    @Value("${app.kafka.topics.streams.output}") String outputTopic;
    @Value("${spring.application.name}") String appName;



    KafkaStreams  createMyStream()  {
        var topology = buildTopology(inputTopic, outputTopic);
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream_"+appName);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getBrokers(kafkaProperties));
        KafkaStreams streams = new KafkaStreams(topology, props);
        log.info("Streams constructed");
        streams.start();
        return streams;
    }

    static Topology buildTopology(String inputTopic, String outputTopic) {
        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder builder = new StreamsBuilder();
        builder
            .stream(inputTopic, Consumed.with(stringSerde, stringSerde))
            .peek((k,v) -> log.info("Observed event: {}", v))
            .mapValues(s -> s.toUpperCase())
            .peek((k,v) -> log.info("Transformed event: {}", v))
            .to(outputTopic, Produced.with(stringSerde, stringSerde));
        var topology = builder.build();
        log.info("Topology constructed {}", topology);
        return  topology;
    }


    @Bean
    public KafkaStreams streams()  {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getBrokers(kafkaProperties));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerializer.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringDeserializer.class);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream("TextLinesTopic");
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
        wordCounts.toStream().to("WordsWithCountsTopic", Produced.with(Serdes.String(), Serdes.Long()));
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return streams;
    }

    private String getBrokers(KafkaProperties kafkaProperties) {
        return String.join(",", kafkaProperties.getBootstrapServers());
    }

}
