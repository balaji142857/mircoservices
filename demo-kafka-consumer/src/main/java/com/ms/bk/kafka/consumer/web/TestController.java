package com.ms.bk.kafka.consumer.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/inquiry")
@RestController
public class TestController {

    private final KafkaStreams streams;

    @GetMapping("stores/{storeName}")
    Map<String, Object> listStoreValues(@PathVariable(required = false) String storeName) {
        log.info("started the streams app");
        if (!StringUtils.hasText(storeName)) {
            storeName = "counts-store";
            log.warn("No store name received, resetting value to {}", storeName);
        }
        ReadOnlyKeyValueStore<String, Long> readOnlyStore = streams.store(StoreQueryParameters
                .fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));
        Map<String, Object> map = new HashMap<>();
        try(KeyValueIterator<String, Long> result = readOnlyStore.all()) {
            while(result.hasNext()){
                var keyValue = result.next();
                log.info("key: {}, value: {}",keyValue.key, keyValue.value);
                map.put(keyValue.key, keyValue.value);
            }
        }
        return map;
    }
}
