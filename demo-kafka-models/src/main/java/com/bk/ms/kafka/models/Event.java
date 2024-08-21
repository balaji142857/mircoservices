package com.bk.ms.kafka.models;


public record Event(EventType eventType, String userName) { }
