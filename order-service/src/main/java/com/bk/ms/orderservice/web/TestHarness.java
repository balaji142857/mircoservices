package com.bk.ms.orderservice.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class TestHarness {

    String inventoryUrl = "http://localhost:8080/inventory-service/test";

    private final RestTemplate restTemplate;

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    @GetMapping("/test2")
    public Map<String,String> test2() {
//        var inventoryResponse = restTemplate.getForEntity(inventoryUrl, Map.class);
        var inventoryResponse = restTemplate.exchange(inventoryUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String,String>>(){});
        var status = inventoryResponse.getStatusCode();
        if (!status.is2xxSuccessful()) {
            log.warn("Received non OK response from inventory service. status: {}, headers: {}",
                    status, inventoryResponse.getHeaders());
            throw new RuntimeException("Invalid response");
        }
        Map<String, String> map = inventoryResponse.getBody();
        log.info("response from inventory service is {}", map);
        map.put("orderKey","orderValue");
        return map;
    }
}
