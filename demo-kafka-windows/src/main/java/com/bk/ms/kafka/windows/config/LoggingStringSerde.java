package com.bk.ms.kafka.windows.config;

import org.apache.kafka.common.serialization.Serdes;

public final class LoggingStringSerde extends Serdes.WrapperSerde<String> {
    public LoggingStringSerde() {
        super(new LoggingStringSerializer(), new LoggingStringDeserializer());
    }
}