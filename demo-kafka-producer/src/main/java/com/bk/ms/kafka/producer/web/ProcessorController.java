package com.bk.ms.kafka.producer.web;

import com.bk.ms.kafka.models.User;
import com.bk.ms.kafka.producer.service.ProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
@Slf4j
public class ProcessorController {

    private final ProcessorService service;

    @GetMapping("/publish")
    String publish(@RequestParam String key,
                   @RequestParam String name,
                   @RequestParam(required = false) Long sal) {
        log.info("Received request for key: {}, name: {}, sal: {}", key, name, sal);
        var user = User.builder()
                .name(name)
                .sal(sal)
                .build();
        return service.process(key, user);
    }
}
