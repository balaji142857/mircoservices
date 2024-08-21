package com.bk.ms.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    @Value("${app}")
    private String hello;

    public String getHello() {
        log.info("property value is: {}", hello);
        return hello;
    }
}
