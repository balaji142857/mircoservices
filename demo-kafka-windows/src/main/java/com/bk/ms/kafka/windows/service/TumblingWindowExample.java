package com.bk.ms.kafka.windows.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;

@Slf4j
public class TumblingWindowExample extends AbstractStreamsPropertiesLoader {

    public static void main(String[] args) {
        TumblingWindowExample instance = new TumblingWindowExample();
        instance.invokeWindowTest();
    }

    @Override
    StreamsBuilder createWindow() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Integer> temperatureReadings = builder.stream("temperature-sensor");

        // Define a 10-minute tumbling window
        TimeWindows tumblingWindows = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(10));

        KTable<Windowed<String>, Integer> averageTemperatures = temperatureReadings
                .peek((k,v) -> log.info("Received event: {} {}",k,v))
                .groupByKey() // Assuming the key is the sensor ID
                .windowedBy(tumblingWindows)
                .aggregate(() -> 0,  // Initializer
                        (aggKey, newValue, aggValue) -> (aggValue + newValue) / 2, // Aggregator
                        Materialized.as("average-temperatures-store")); // State store name
        log.info("Kafka table is {}", averageTemperatures);
        // Further processing or output the results to a topic
        return  builder;
    }
}