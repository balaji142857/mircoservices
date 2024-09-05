package com.bk.ms.kafka.windows.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import java.time.Duration;

@Slf4j
public class SlidingWindowExample extends AbstractStreamsPropertiesLoader {

    public static void main(String[] args) {
       SlidingWindowExample instance = new SlidingWindowExample();
       instance.invokeWindowTest(5);
    }

    StreamsBuilder createWindow() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> clicks = builder.stream("clicks-topic");

        // Define the sliding window
        TimeWindows slidingWindow = TimeWindows
                .ofSizeWithNoGrace(Duration.ofMinutes(30))
                .advanceBy(Duration.ofMinutes(10));

        // Count clicks in the sliding window
        KTable<Windowed<String>, Long> clickCounts = clicks
                .groupByKey()
                .windowedBy(slidingWindow)
                .count(Materialized.as("clicks-count-store"));

        // Further processing or output the results to a topic
        log.info("Kafka table is {}", clickCounts);
        return builder;
    }
}