package com.bk.ms.kafka.windows.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;

@Slf4j
public class SessionWindowExample extends  AbstractStreamsPropertiesLoader{

    public static void main(String[] args) {
        SessionWindowExample instance = new SessionWindowExample();
        instance.invokeWindowTest();
    }


    @Override
    StreamsBuilder createWindow() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> playerActions = builder.stream("player-actions");

        Duration inactivityGap = Duration.ofMinutes(10); // Define inactivity gap for session windows

        KTable<Windowed<String>, Long> sessionCounts = playerActions
                // Assuming the key is the player ID
                 .groupByKey()
                // Apply session windowing
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(inactivityGap))
                // Count events in each session
                .count(Materialized.as("session-counts-store"));

        // Further processing or output the results to a topic
        log.info("Kafka table is {}", sessionCounts);
        return builder;
    }
}