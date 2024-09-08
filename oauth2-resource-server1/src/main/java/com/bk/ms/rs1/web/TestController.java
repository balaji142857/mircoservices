package com.bk.ms.rs1.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TestController {

    @GetMapping("/products")
    public List<Product> getProducts() {
        return Arrays.asList(
                new Product(1L, "I Pad"),
                new Product(2L, "I Phone"),
                new Product(3L, "MacBook"));
    }
}

record Product (Long id, String name) { }