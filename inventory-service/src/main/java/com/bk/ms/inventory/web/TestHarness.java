package com.bk.ms.inventory.web;

import com.bk.ms.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestHarness {

    private final InventoryService service;

    @GetMapping("/test")
    public Map<String,String> test() {
        var serviceResult =  service.getHello();
        log.info("service response: {}", serviceResult);
        var result = Map.of("app",serviceResult);
        log.info("API response: {}", result);
        return result;
    }
}

