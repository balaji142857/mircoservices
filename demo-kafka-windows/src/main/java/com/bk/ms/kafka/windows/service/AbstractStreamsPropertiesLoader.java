package com.bk.ms.kafka.windows.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public abstract class AbstractStreamsPropertiesLoader {

    void invokeWindowTest() {
        Properties props = new Properties();
        String propFile = "application.properties";
        try (InputStream propertiesInputStream = getClass().getClassLoader().getResourceAsStream(propFile)) {
            props.load(propertiesInputStream);
            StreamsBuilder builder = createWindow();
            KafkaStreams streams = new KafkaStreams(builder.build(), props);
            streams.start();
            log.info("streams app has started..");
        } catch (IOException e) {
            log.error("Error occurred while setting up streams",e);
        }
    }

    abstract StreamsBuilder createWindow();
}
