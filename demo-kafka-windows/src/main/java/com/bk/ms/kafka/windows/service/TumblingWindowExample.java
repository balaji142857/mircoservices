package com.bk.ms.kafka.windows.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.*;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class TumblingWindowExample extends AbstractStreamsPropertiesLoader {

    private final String storeName =  "average-temperatures-store";

    public static void main(String[] args) {
        TumblingWindowExample instance = new TumblingWindowExample();
        instance.invokeWindowTest(3);
    }

    @Override
    StreamsBuilder createWindow() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> temperatureReadings = builder.stream("temperature-sensor",
                Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()));

        // Define one-minute tumbling window
        TimeWindows tumblingWindows = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1));

        temperatureReadings
                .peek((k,v) -> log.info("Received event: {} {}",k,v))
                .mapValues(v -> v.toLowerCase().split("\\W+").length)
                .groupByKey() // Assuming the key is the sensor ID
                .windowedBy(tumblingWindows)
                            // Initializer
                            .aggregate(() -> 0,
                            // Aggregator
                            (aggKey, newValue, aggValue) -> (aggValue + newValue) / 2,
                            // State store name
                            Materialized.as(storeName))
                .toStream()
                .peek((k,v) -> StreamExample.logMessage("Window output",k,v));
// TODO generics , Produced.with(new WindowedSerdes.TimeWindowedSerde(), new Serdes.LongSerde())
//                .to("temperature-sensor-windowed");
        // Further processing or output the results to a topic
        return  builder;
    }

    @Override
    protected  void preCloseActions() {
        var storeType = QueryableStoreTypes.timestampedWindowStore();
        ReadOnlyWindowStore<Object, ValueAndTimestamp<Object>> keyValueStore =
                streams.store(StoreQueryParameters.fromNameAndType(storeName, storeType));
        try (KeyValueIterator<Windowed<Object>, ValueAndTimestamp<Object>> it = keyValueStore.all()) {
            while(it.hasNext()) {
                KeyValue<Windowed<Object>, ValueAndTimestamp<Object>> kv = it.next();
                var key = kv.key;
                var value = kv.value;
                log.info("State store Key: {}, startTime: {} endTime: {} eventTime: {} Value: {}",
                        key.key(),  key.window().startTime(), key.window().endTime(),
                        Instant.ofEpochMilli(value.timestamp()), value.value());
            }
        }
    }

    @Override
    protected void updateProperties() {
//        props.put("default.value.serde","org.apache.kafka.common.serialization.Serdes$StringSerde");
    }
}