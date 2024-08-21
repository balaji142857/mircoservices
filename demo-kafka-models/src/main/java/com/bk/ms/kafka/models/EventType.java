package com.bk.ms.kafka.models;

import lombok.Getter;

@Getter
public enum EventType {

    LOGOUT, LOGIN, BROWSE, DOWNLOAD, INVALID_LOGIN, PAYMENT
}
