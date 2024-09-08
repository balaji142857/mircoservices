package com.bk.ms.rs2.web;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private WebClient webClient;

    @GetMapping("/products")
    public List<Product> getProducts() {
        log.info("ResourceServer2 received request for products");
        return this.webClient
                .get()
                .uri("http://127.0.0.1:8090/products")
                .attributes(clientRegistrationId("products-client-client-credentials"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
                .block();
    }

    @PostConstruct
    void init () {
        Map<String, Object> dummyMap = new HashMap<>();
        Consumer<Map<String, Object>> s = clientRegistrationId("products-client-client-credentials");
        s.accept(dummyMap);
        log.info("clientRegistration attributes : {}",dummyMap);

    }
}

record Product (Long id, String name) { }