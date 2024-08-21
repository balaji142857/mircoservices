package com.bk.ms.kafka.windows.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class LoggingStringDeserializer implements Deserializer<String>{
    private String encoding;

    public LoggingStringDeserializer() {
        this.encoding = StandardCharsets.UTF_8.name();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.deserializer.encoding" : "value.deserializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get("deserializer.encoding");
        }

        if (encodingValue instanceof String) {
            this.encoding = (String)encodingValue;
        }

    }

    public String deserialize(String topic, byte[] data) {
        try {
            var resp =  data == null ? null : new String(data, this.encoding);
            log.info("Deserialized value: {}", resp);
            return resp;
        } catch (UnsupportedEncodingException var4) {
            throw new SerializationException("Error when deserializing byte[] to string due to unsupported encoding " + this.encoding);
        }
    }

    public String deserialize(String topic, Headers headers, ByteBuffer data) {
        if (data == null) {
            return null;
        } else {
            try {
                var resp =  data.hasArray() ? new String(data.array(), data.position() + data.arrayOffset(), data.remaining(), this.encoding) : new String(Utils.toArray(data), this.encoding);
                log.info("Deserialized value: {}", resp);
                return resp;
            } catch (UnsupportedEncodingException var5) {
                throw new SerializationException("Error when deserializing ByteBuffer to string due to unsupported encoding " + this.encoding);
            }
        }
    }
}

