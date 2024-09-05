package com.bk.ms.kafka.windows.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;


@Slf4j
public class HoppingWindowExample extends  AbstractStreamsPropertiesLoader{
    public static void main(String[] args) {
        HoppingWindowExample instance = new HoppingWindowExample();
        instance.invokeWindowTest(10);
    }



    @Override
    StreamsBuilder createWindow() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> pageViews = builder.stream("page-views");

        TimeWindows hoppingWindows = TimeWindows
                // Window size
                .ofSizeWithNoGrace(Duration.ofMinutes(5))
                // Advance interval
                .advanceBy(Duration.ofMinutes(1));


        KTable<Windowed<String>, Long> pageViewCounts = pageViews
                // Group by page view event value (or some logic)
                .groupBy((key, value) -> value)
                .windowedBy(hoppingWindows)
                // Store name for the state store
                .count(Materialized.as("page-view-counts-store"));

        // Further processing...
        log.info("Kafka table is {}", pageViewCounts);
        return builder;
    }
}