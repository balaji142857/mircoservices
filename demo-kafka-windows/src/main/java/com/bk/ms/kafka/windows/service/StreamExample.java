package com.bk.ms.kafka.windows.service;

import com.bk.ms.kafka.windows.config.LoggingStringSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;


@Slf4j
public class StreamExample {

    // key - scenario
    // value: Pair(left: function, right: consumer)
    static final Map<String,
            Pair<Function<Properties, Pair<StreamsBuilder, Properties>>, BiConsumer<KafkaStreams, String>>> map = new HashMap<>();

    static final BiConsumer<KafkaStreams, String> noOpsConsumer = (a, b) -> {};
    static final BiConsumer<KafkaStreams, String> logStateStoreValues =
            (streams, storePrefix) ->  logStateStore(streams, storePrefix+"Store", QueryableStoreTypes.keyValueStore());

    static {
        map.put("stream", new ImmutablePair<>(StreamExample::stream, noOpsConsumer));
        map.put("table", new ImmutablePair<>( StreamExample::table, logStateStoreValues));
        map.put("globalKTable", new ImmutablePair<>( StreamExample::globalKTable, logStateStoreValues));
        map.put("state", new ImmutablePair<>( StreamExample::state, logStateStoreValues));
        map.put("streamStreamJoin", new ImmutablePair<>( StreamExample::streamStreamJoin, noOpsConsumer));
        map.put("streamTableJoin", new ImmutablePair<>( StreamExample::streamTableJoin, noOpsConsumer));
        map.put("tableTableJoin", new ImmutablePair<>( StreamExample::tableTableJoin, noOpsConsumer));
        map.put("streamGlobalTableJoin", new ImmutablePair<>( StreamExample::streamTableJoin, noOpsConsumer));
    }


    public static void main(String[] args) throws Exception {
        log.info("Invoked using args: {}", (Object[]) args);
        if (args == null || args.length == 0) {
            log.error("Usage: java StreamExample scenario1 scenario2 scenario3 [scenarioN...]");
            log.info("Applicable scenarios: {}", String.join(",", map.keySet()));
            System.exit(-1);
        }


        for (String arg : args) {
            Properties props = loadProperties();
            var outerPair = map.get(arg);
            Function<Properties, Pair<StreamsBuilder, Properties>> scenario = outerPair.getLeft();
            if (null == scenario) {
                throw new RuntimeException("Illegal scenario " + arg);
            }
            var pair = scenario.apply(props);

            // execute the scenario
            StreamsConfig sc = new StreamsConfig(pair.getRight());
            Topology topology = pair.getLeft().build(pair.getRight());
            log.info("Topology is: {}",topology.describe());
            KafkaStreams streams = new KafkaStreams(topology, sc);
            streams.start();
            log.info("streams app has started..");


            // wait for five minutes & run the consumer - to log whatever is required for the scenario
            sleep(3);
            if (null != outerPair.getRight()) {
                outerPair.getRight().accept(streams, arg);
            }
            streams.close();
        }
    }



    /**
     * topic-a -> prepend "processed to value" ->  topic-b
     */
    private static Pair<StreamsBuilder, Properties> stream(Properties props)  {
        StreamsBuilder sb = new StreamsBuilder();
        sb.stream("topic-a")
                .peek((k,v) -> StreamExample.logMessage("Received Message: ",k,v))
                .mapValues(v -> "processed " + v)
                .peek((k,v) -> StreamExample.logMessage("Processed Message: ",k,v))
                .to("topic-b");
        // just override required props values defined in application.properties
         props.put("default.value.serde","org.apache.kafka.common.serialization.Serdes$StringSerde");
        return new ImmutablePair<>(sb, props);
    }



    /**
     * Stream(topic-d) -> KTable(tableStore) -> Stream(topic-b)
     */
    private static Pair<StreamsBuilder, Properties> table(Properties props) {
        StreamsBuilder sb = new StreamsBuilder();
        sb.table(
                "topic-d",
                      Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()),
                      Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("tableStore")
                )
                .toStream(Named.as("tableAsStream"))
                .peek((k,v) -> StreamExample.logMessage("table as stream: ",k,v))
                .to("topic-b", Produced.with(new Serdes.StringSerde(), new Serdes.StringSerde()));

        setStateStoreDir(props);
        // just override required props values defined in application.properties
        props.put("default.value.serde","org.apache.kafka.common.serialization.Serdes$StringSerde");
        return new ImmutablePair<>(sb, props);
    }




    /**
     * Stream(topic-d) -> GlobalKTable(globalKTable)
     */
    // FIXME - if starting two instances - both get all the messages
    // second thoughts -- same config for kTable kafka distributes messages across the instances
    // so for globalKTable this might be the intended behavior to capture all the events across partitions
    private static Pair<StreamsBuilder, Properties> globalKTable(Properties props)  {
        StreamsBuilder sb = new StreamsBuilder();
        String storeName = sb.globalTable(
                "topic-d",
                Consumed.with(new LoggingStringSerde(), new LoggingStringSerde()),
                Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("globalKTableStore")
        ).queryableStoreName();
        log.info("Global KTable storeName is {}", storeName);


        setStateStoreDir(props);
        props.put("default.value.serde","org.apache.kafka.common.serialization.Serdes$StringSerde");
        return new ImmutablePair<>(sb, props);
    }


    private static Pair<StreamsBuilder, Properties> state(Properties props) {
        StreamsBuilder sb = new StreamsBuilder();
        Materialized<String, Long, KeyValueStore<Bytes, byte[]>> store = Materialized.as("stateStore");
        sb.stream("topic-d", Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()))
                .peek((k,v) -> StreamExample.logMessage("msg: ",k,v))
                .flatMapValues(v -> Arrays.asList(v.toLowerCase().split("\\W+")))
                .peek((k,v) -> StreamExample.logMessage("msg after split: ",k,v))
                .groupBy((k,v) -> v)
                .count(Named.as("stateStore"), store)
                .toStream()
                .peek((k,v) -> StreamExample.logMessage("word count: ",k,v))
                .to("word-count", Produced.with(Serdes.String(), Serdes.Long()));
        setStateStoreDir(props);
        props.put("default.value.serde","org.apache.kafka.common.serialization.Serdes$StringSerde");
        return new ImmutablePair<>(sb, props);
    }



    /**
     * topic-a +  topic-b -> topic-c
     */
    private static Pair<StreamsBuilder, Properties> streamStreamJoin(Properties props) {
        StreamsBuilder sb = new StreamsBuilder();
        KStream<String, String> streamB = sb.stream("topic-b", Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()));
        KStream<String, String> streamA = sb.stream("topic-a", Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()));
        streamA
                .peek((k,v) -> StreamExample.logMessage("Stream A msg: ",k,v))
                .join(
                        streamB,
                        (va, vb) -> va +" -> got converted to -> " + vb,
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(3))
                )
                .peek((k,v) -> StreamExample.logMessage("Joined msg: ",k,v))
                .to("topic-c");

        // just override required props values defined in application.properties
        props.put("default.value.serde","org.apache.kafka.common.serialization.Serdes$StringSerde");
        return new ImmutablePair<>(sb, props);
    }



    private static Pair<StreamsBuilder, Properties> streamTableJoin(Properties props) {
        StreamsBuilder sb = new StreamsBuilder();
        KTable<String, String> tableA = sb.table("topic-a", Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()));
        KStream<String, String> streamB = sb.stream("topic-b", Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()));
        streamB
                .peek((k,v) -> StreamExample.logMessage("Stream A msg: ",k,v))
                .join(
                        tableA,
                        (va, vb) -> va +" -> got converted to -> " + vb
                )
                .peek((k,v) -> StreamExample.logMessage("Joined msg: ",k,v))
                .to("topic-d");
        log.info("SB is {}",sb);

        // just override required props values defined in application.properties
        props.put("default.value.serde","org.apache.kafka.common.serialization.Serdes$StringSerde");
        return new ImmutablePair<>(sb, props);
    }


    private static Pair<StreamsBuilder, Properties> tableTableJoin(Properties props) {
        StreamsBuilder sb = new StreamsBuilder();
        KTable<String, String> tableA = sb.table("topic-a", Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()));
        KTable<String, String> tableB = sb.table("topic-b", Consumed.with(new Serdes.StringSerde(), new Serdes.StringSerde()));
        tableB
               .join(
                        tableA,
                        (va, vb) -> va +" -> got converted to -> " + vb
               )
               .toStream(Named.as("tableA-tableB-join"))
               .to("topic-d", Produced.with(new Serdes.StringSerde(), new Serdes.StringSerde()));

        // just override required props values defined in application.properties
        props.put("default.value.serde","org.apache.kafka.common.serialization.Serdes$StringSerde");
        return new ImmutablePair<>(sb, props);
    }


    private static Pair<StreamsBuilder, Properties> streamGlobalTableJoin(Properties props) {
      return null;
    }


    public static void logMessage(String prefix, Object key, Object value) {
        log.info("{} (k,v): ({},{})",prefix, key,value);
    }


    public static <K,V> void logStateStore(KafkaStreams streams,
                                    String storeName,
                                    QueryableStoreType<ReadOnlyKeyValueStore<K,V>> storeType) {
        ReadOnlyKeyValueStore<K, V> keyValueStore =
                streams.store(StoreQueryParameters.fromNameAndType(storeName, storeType));
        try (KeyValueIterator<K, V> it = keyValueStore.all()) {
            while(it.hasNext()) {
                KeyValue<K, V> kv = it.next();
                log.info("From state store Key: {}, Value: {}",kv.key, kv.value);
            }
        }
    }


    private static void setStateStoreDir(Properties props) {
        // setup a state dir in temp and delete the file on exit
        try {
            Path tempDirPath = Files.createTempDirectory("tmpDirPrefix");
            tempDirPath.toFile().deleteOnExit();
            String tempDir = tempDirPath.toFile().getAbsolutePath();
            log.info("Temp directory for state storage is {}", tempDir);
            props.put(StreamsConfig.STATE_DIR_CONFIG,tempDir);
            props.put(ConsumerConfig.GROUP_ID_CONFIG,"tableGroup");
        } catch (Exception e) {
            log.error("Unable to set a random temp directory for state store, Starting multiple instances is bound to fail", e);
        }
    }

    public static void sleep(int sleepTimeInMinutes) {
        try {
            TimeUnit.MINUTES.sleep(sleepTimeInMinutes);
        } catch (InterruptedException e) {
            log.error("Error occurred while keep the thread in sleep state", e);
            Thread.currentThread().interrupt();
        }
    }

    public static Properties loadProperties() {
        Properties props = new Properties();
        String propFile = "application.properties";
        try {
            InputStream propertiesInputStream = StreamExample.class.getClassLoader().getResourceAsStream(propFile);
            props.load(propertiesInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load the property file " + propFile, e);
        }
        return props;
    }
}
