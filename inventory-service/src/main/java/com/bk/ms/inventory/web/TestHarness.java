package com.bk.ms.inventory.web;

import com.bk.ms.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestHarness {

    private final InventoryService service;

    @GetMapping("/test")
    public String test() {
        return service.getHello();
    }
}
