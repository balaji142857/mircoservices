package com.bk.ms.kafka.windows.config;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class LoggingStringSerializer implements Serializer<String> {
    private String encoding;

    public LoggingStringSerializer() {
        this.encoding = StandardCharsets.UTF_8.name();
    }

    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get("serializer.encoding");
        }

        if (encodingValue instanceof String) {
            this.encoding = (String)encodingValue;
        }

    }

    public byte[] serialize(String topic, String data) {
        try {
            log.info("Serializing value: {}", data);
            return data == null ? null : data.getBytes(this.encoding);
        } catch (UnsupportedEncodingException var4) {
            throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + this.encoding);
        }
    }
}
