package com.bk.ms.kafka.windows.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractStreamsPropertiesLoader {

    protected KafkaStreams streams;

    protected  Properties props;

    abstract StreamsBuilder createWindow();

    protected void preCloseActions() {}

    protected void updateProperties() { }

    void invokeWindowTest(int sleepTimeInMinutes) {
        props = StreamExample.loadProperties();
        StreamsBuilder builder = createWindow();
        updateProperties();
        streams = new KafkaStreams(builder.build(), props);
        streams.start();
        log.info("streams app has started..");
        StreamExample.sleep(sleepTimeInMinutes);
        preCloseActions();
        streams.close();
    }
}
